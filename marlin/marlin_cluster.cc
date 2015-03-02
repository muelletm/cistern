#include "basic/std.h"
#include "basic/stl-basic.h"
#include "basic/stl-utils.h"
#include "basic/opt.h"
#include <unistd.h>

vector< OptInfo<bool> > bool_opts;
vector< OptInfo<int> > int_opts;
vector< OptInfo<real> > double_opts;
vector< OptInfo<string> > string_opts;

opt_define_string_req(unigram_file_, "words", "Text file with words.");
opt_define_string_req(bigram_file_, "bigrams", "Text file with bigrams.");
opt_define_string_req(output_,    "output", "Output.");

opt_define_double(alpha_, "alpha", 0.0,         "Character Model Strength [0,1].");
opt_define_int(num_classes_,        "c", 1000,                    "Number of clusters.");
opt_define_int(num_steps_,        "steps", -1,                    "Number of steps or -1 to run until convergence.");
opt_define_int(rand_seed,    "rand", time(NULL)*getpid(),  "Number to call srand with.");
opt_define_int(verbose_,    "v", 2,  "Verbosity level.");
opt_define_int(sanity_checks_,    "sanity", 0,  "Sanity Check level. Makes things slow!");
opt_define_int(cache_size_,    "cache-size", 100000,  "N up to which to cache n * log(n).");

const real REAL_NEG_INF = -1e99;

struct UpdateResult {
  int current_w;
  int current_c;
  int best_c;
  real best_delta;
  DoubleVec deltas;

  UpdateResult() {
    deltas.resize(num_classes_);
  }

};

struct Entry{
  int item;
  int count;
};

bool compare_entry(const Entry& entry, const Entry& other) {
  return entry.item < other.item;
}

typedef vector<Entry> EntryVec;
typedef vector<EntryVec> EntryVecVec;

void addToSparseEntryVec(EntryVec& vector, int klass, int count) {
  Entry entry;
  entry.item = klass;
  entry.count = count;

  EntryVec::iterator pos = lower_bound (vector.begin(), vector.end(), entry, compare_entry);

  if (pos != vector.end() && pos->item == klass) {
    pos->count += count;
  } else {
    vector.insert(pos, entry);
  }
  
}

struct BiEntry{
  int item;
  int item2;
  int count;
};

typedef vector<BiEntry> BiEntryVec;
typedef vector<BiEntryVec> BiEntryVecVec;

// Observed counts
EntryVecVec left_context_;
EntryVecVec right_context_;

IntVec word_counts_;

IntVec class_class_counts_;
IntVec class_counts_;
IntVecVec class_char_char_counts_;
IntVecVec class_char_counts_;

IntVec word_assignment_;

int num_words_;
int num_chars_;

// Whether a character bigram model is used. Depends on alpha.
bool character_;

StringVec word_forms_;
IntVecVec word_chars_;

// These data structures are needed to make update() efficient.

// Number of times a class has been seen left (right) of the current word.
EntryVec left_class_counts_;
EntryVec right_class_counts_;

EntryVec left_tri_class_counts_;
EntryVec center_tri_class_counts_;
EntryVec right_tri_class_counts_;

// Current word
int current_word_;

// Character bigrams and unigrams occuring in current word.
EntryVec char_bigram_counts_;
EntryVec char_unigram_counts_;

// Class bigrams affected by current class change.
IntVec current_class_class_counts_;

// Number of times a word cooccurs with itself.
int word_word_count_;
int word_word_word_count_;
EntryVec word_word_x_counts_;
EntryVec word_x_word_counts_;
EntryVec x_word_word_counts_;

// Constant term of the Likelihood that depends on word form counts
real word_ll_term_;

DoubleVec nlogn_cache_;

real nlogn(int n) {
  assert (n >= 0);

  if (n == 0) {
    return 0;
  }

  if (n - 1 < cache_size_) {
    return nlogn_cache_[n - 1];
  }

  return n * log(n);
}

int classIndex(int i, int j) {
  assert (i >= 0 && i < num_classes_);
  assert (j >= 0 && j < num_classes_);
  int index = i * num_classes_ + j;
  assert (index >= 0 && index < num_classes_ * num_classes_);
  return index;
}

void addCharBigram(int klass, int c, int k, int count) {
  int index = c * num_chars_ + k;
  class_char_char_counts_[klass][index] += count;
}

void incrementChars(int word, int klass, int factor) {
  int last = 0;
  IntVec& chars = word_chars_[word];

  forvec(_, int, c, chars) {
    addCharBigram(klass, last, c, factor * word_counts_[word]);

    class_char_counts_[klass][c] += factor * word_counts_[word];
    assert (class_char_counts_[klass][c] >= 0);

    last = c;
  }

  addCharBigram(klass, last, 0, factor * word_counts_[word]);

  class_char_counts_[klass][0] += factor * word_counts_[word];
  assert (class_char_counts_[klass][last] >= 0);
}

void assignToZero() {
  
  fill(word_assignment_.begin(), word_assignment_.end(), 0);

  for (int word = 0; word < num_words_; word++) {
    class_counts_[0] += word_counts_[word];
    if (character_) {
      if (word > 0) {
	incrementChars(word, 0, +1);
      }
    }
  }
  class_class_counts_[0] = class_counts_[0];
}

void addTagTagCount(int klass, int cclass, int count) {
  class_class_counts_[classIndex(klass, cclass)] += count;
}

void incrementBigrams(int word, int klass, int factor) {
  forvec (_, Entry, entry, left_context_[word]) {
    int cword = entry.item;
    if (cword != word) {
      int cclass = word_assignment_[cword];
      addTagTagCount(cclass, klass, factor * entry.count);
    } else {
      addTagTagCount(klass, klass, factor * entry.count);
    }
  }

  forvec (_, Entry, entry, right_context_[word]) {
    int cword = entry.item;
    if (cword != word) {
      int cclass = word_assignment_[cword];
      addTagTagCount(klass, cclass, factor * entry.count);
    }
  }
}

void increment(int word, int klass, int factor) {

  assert (word > 0);
  assert (klass > 0 || factor < 0);

  class_counts_[klass] += factor * word_counts_[word];

  incrementBigrams(word, klass, factor);

  if (character_) {
    incrementChars(word, klass, factor);
  }

  word_assignment_[word] = klass;
}

void randomInit() {
  assert (num_words_ > num_classes_);

  assignToZero();

  int half_num_classes = num_classes_ / 2;

  for (int word = 1; word < half_num_classes; word++) {
    increment(word, 0, -1);
    increment(word, word, +1);
  }

  for (int word = half_num_classes; word < num_words_; word++) {
    int klass = half_num_classes + mrand((int)ceil(num_classes_ / 2.));
    increment(word, 0, -1);
    increment(word, klass, +1);
  }
}

void strtok(StringVec& vec, string string, char delim) {
  uint last = 0;
  for (uint i=0; i<string.length(); i++) {
    char c = string[i];
    if ((delim == 0 && isspace(c)) || (delim > 0 && delim == c )) {
	int length = i - last;
	if (length > 0)
	  vec.push_back(string.substr(last, length));
	last = i + 1;
    }
  }

  if (last < string.length()) {
    vec.push_back(string.substr(last, string.length() - last));
  }
}

void readWordForms() {
  ifstream in(unigram_file_.c_str());
  string buf;
  while(getline(in, buf)) {
    StringVec tokens;
    strtok(tokens, buf, (char) 0);
    const string& word_form = tokens[0];
    word_forms_.push_back(word_form);
  }
  num_words_ = word_forms_.size();
}

void readBigrams() {
  ifstream in(bigram_file_.c_str());
  string buf;
  int word = 0;

  while(getline(in, buf)) {
    EntryVec& cwords = right_context_[word];

    StringVec line;
    strtok(line, buf, (char) 0);

    forvec (_, string, pair_string, line) {
      Entry entry;

      StringVec tokens;
      strtok(tokens, pair_string, ':');

      const string& word_string = tokens[0];
      const string& count_string = tokens[1];
  
      int cword = atoi(word_string.c_str());
      int count = atoi(count_string.c_str());

      // Add to right contexts
      entry.item = cword;
      entry.count = count;
      cwords.push_back(entry);

      // Add to left contexts     
      entry.item = word;
      left_context_[cword].push_back(entry);

      word_counts_[word] += count;
    }

    word++;
  }


  // Check consistency
  for (word = 0; word < num_words_; word++) {
    int count = word_counts_[word];

    int left_count = 0;
    forvec (_, const Entry&, entry, left_context_[word]) {
      left_count += entry.count;
    }

    int right_count = 0;
    forvec (_, const Entry&, entry, right_context_[word]) {
      right_count += entry.count;
    }

    assert (count == left_count && count == right_count);
  }

}

void readData() {
  readWordForms();

  if (alpha_ > 1e-5) {
    character_ = true;
  } else {
    character_ = false;
  }

  // Fill nlogn cache.
  nlogn_cache_.resize(cache_size_);
  for (int i=0; i<cache_size_; i++) {
    nlogn_cache_[i] = (i + 1) * log(i + 1);
  }

  // Init data structures.

  class_class_counts_.resize(num_classes_ * num_classes_);  
  current_class_class_counts_.resize(num_classes_ * num_classes_);

  left_context_.resize(num_words_);
  right_context_.resize(num_words_);

  word_counts_.resize(num_words_);
  class_counts_.resize(num_classes_);
  word_assignment_.resize(num_words_);
  word_forms_.resize(num_words_);

  readBigrams();

  if (character_) {

    typedef unordered_map<char, int> CIMap;

    CIMap table;
    table['^'] = 0;

    word_chars_.resize(num_words_);

    for (int w = 1; w < num_words_; w++) {
      const string& form = word_forms_[w];

      IntVec& chars = word_chars_[w];
      chars.resize(form.length());

	for (uint i = 0; i < form.length(); i++) {
	  char c = form[i];
	  assert (c != '^');

	  int k = table[c];

	  if (k == 0) {
	    k = table.size();
	    table[c] = k;
	  }
	  
	  chars[i] = k;
	}
      
    }

    num_chars_ = table.size();

    matrix_resize(class_char_char_counts_, num_classes_, num_chars_* num_chars_);
    matrix_resize(class_char_counts_, num_classes_, num_chars_);

  }

  word_ll_term_ = 0;
  for (int w = 1; w < num_words_; w++) {
    word_ll_term_ += nlogn(word_counts_[w]);
  }
}

real calcCharLikelihood(int klass) {
  real ll = 0;

  // Bigram Counts
  for (int c = 0; c < num_chars_; c++) {
    for (int k = 0; k < num_chars_; k++) {
      int index = c * num_chars_ + k;
      ll += nlogn(class_char_char_counts_[klass][index]);
    }
  }

  // Unigram Counts
  for (int c = 0; c < num_chars_; c++) {
    ll -= nlogn(class_char_counts_[klass][c]);
  }

  return ll;
}

real calcLikelihood() {
  real ll = 0;

  // Bigram Context Counts.
  real ll_b = 0;

  for (int c = 0; c < num_classes_; c++) {
    for (int k = 0; k < num_classes_; k++) {
      ll_b += nlogn(class_class_counts_[classIndex(c, k)]);
    }
  }
  
  for (int c = 0; c < num_classes_; c++) {
    ll_b -= nlogn(class_counts_[c]);
  }

  ll += ll_b;

  // Unigram Word Emission Counts.
  real ll_w = word_ll_term_;
  for (int c = 0; c < num_classes_; c++) {
    ll_w -= nlogn(class_counts_[c]);
  }
  ll += (1.0 - alpha_) * ll_w;

  // Character Model Counts.

  real ll_c = 0;
  if (character_) {
    for (int c = 0; c < num_classes_; c++) {
      ll_c += calcCharLikelihood(c);
    }
  }
  ll += alpha_ * ll_c;

  return ll;
}

void setCharCounts(int w) {
   char_bigram_counts_.clear();
   char_unigram_counts_.clear();

   const IntVec& chars = word_chars_[w];

   int last = 0;
   forvec (_, int, c, chars) {
     addToSparseEntryVec(char_bigram_counts_, last * num_chars_ + c, 1);
     addToSparseEntryVec(char_unigram_counts_, c, 1);
     last = c;
   }

   addToSparseEntryVec(char_bigram_counts_, last * num_chars_ + 0, 1);
   addToSparseEntryVec(char_unigram_counts_, 0, 1);
}

void setContextClassCount(int word) {
  left_class_counts_.clear();
  right_class_counts_.clear();
  word_word_count_ = 0;

  forvec (_, Entry, entry, left_context_[word]) {
    int cword = entry.item;
    if (cword != word) {
      int cclass = word_assignment_[cword];

      addToSparseEntryVec(left_class_counts_, cclass, entry.count);
    } else {
      word_word_count_ = entry.count;
    }
  }

  forvec (_, Entry, entry, right_context_[word]) {
    int cword = entry.item;
    if (cword != word) {
      int cclass = word_assignment_[cword];
      addToSparseEntryVec(right_class_counts_, cclass, entry.count);
    }
  }
}

void setCurrentWord(int w) {
  current_word_ = w;

  setContextClassCount(w);

  if (character_)
    setCharCounts(w);
}

real calcCharDelta(int klass) {
  int wcount = word_counts_[current_word_];
  real delta = 0;

  forvec (_, const Entry&, entry, char_bigram_counts_) {
    int old_count = class_char_char_counts_[klass][entry.item];
    int new_count = old_count + entry.count * wcount;
    delta += nlogn(new_count) - nlogn(old_count);
  }

  forvec (_, const Entry&, entry, char_unigram_counts_) {
    int old_count = class_char_counts_[klass][entry.item];
    int new_count = old_count + entry.count * wcount;
    delta -= nlogn(new_count) - nlogn(old_count);
  }
   
  return delta;
}

real calcLocalDelta(int c, int k, int count) {
  assert (k < num_classes_);
  assert (c < num_classes_);
  assert (count >= 0);
  int index = classIndex(c, k);
  int oldcount = class_class_counts_[index] + current_class_class_counts_[index];
  int newcount = oldcount + count;
  current_class_class_counts_[index] += count;
  return nlogn(newcount) - nlogn(oldcount);
}

void setZero(int c, int k) {
  assert (k < num_classes_);
  assert (c < num_classes_);
  int index = classIndex(c, k);
  current_class_class_counts_[index] = 0;
}

real calcBigramDelta(int klass) {
  real delta = 0;

  forvec (_, Entry, entry, left_class_counts_) {
    delta += calcLocalDelta(entry.item, klass, entry.count);
  }

  forvec (_, Entry, entry, right_class_counts_) {
    delta += calcLocalDelta(klass, entry.item, entry.count);
  }

  delta += calcLocalDelta(klass, klass, word_word_count_);

  forvec (_, Entry, entry, left_class_counts_) {
    setZero(entry.item, klass);
  }

  forvec (_, Entry, entry, right_class_counts_) {
    setZero(klass, entry.item);
  }

  setZero(klass, klass);

  return delta;
}

real calcDelta(int klass) {
  int wcount = word_counts_[current_word_];
  real delta = 0;

  real bigram_delta = calcBigramDelta(klass);

  real delta_b = 0;
  delta_b += bigram_delta;
  delta_b -= (nlogn(class_counts_[klass] + wcount ) - nlogn(class_counts_[klass]));
  delta += delta_b;

  // Word Emission
  delta -= (1.0 - alpha_) * (nlogn(class_counts_[klass] + wcount ) - nlogn(class_counts_[klass]));

  // Character Bigram Emission
  if (character_) {
    delta += alpha_ * calcCharDelta(klass);
  }

  return delta;
}


void update(UpdateResult &r) {
  setCurrentWord(r.current_w);

  for (int c = 1; c < num_classes_; c++) {
    real delta = calcDelta(c);
    r.deltas[c] = delta;

    if (delta > r.best_delta) {
      r.best_delta = delta;
      r.best_c = c;
    }    
  }
}

int update() {
  int swaps = 0;

  real current_ll = calcLikelihood();

  UpdateResult r;

  for (int w = 1; w < num_words_; w++) {
    r.current_w = w;
    r.current_c = word_assignment_[w];
    fill(r.deltas.begin(), r.deltas.end(), 0.0);
    r.best_delta = REAL_NEG_INF;
    r.best_c = -1;

    increment(w, r.current_c, -1);
    update(r);

    current_ll = current_ll - r.deltas[r.current_c] + r.deltas[r.best_c];
    increment(w, r.best_c, +1);

    if (r.current_c != r.best_c) {
      swaps += 1;
    }

    if (sanity_checks_ > 0) {
      real actual_ll = calcLikelihood();
      real delta = fabs((actual_ll - current_ll) / current_ll);
      if (delta > 1e-5) {
	cerr << "Sanity check failed: " << delta << " " << actual_ll << " " << current_ll << endl;
	assert (false);
      }
    }

    if (verbose_ > 1 && w % (num_words_ / 4) == 0) {
      fprintf(stderr, "W:%6d / %6d LL: %g Swaps: %5d\n", w, num_words_, current_ll, swaps);
    }
  }

  if (verbose_ > 0) {
    fprintf(stderr, "W:%6d / %6d LL: %g Swaps: %5d\n", num_words_, num_words_, current_ll, swaps);
  }

  return swaps;
}

void writeAssignment() {
  ofstream os;
  os.open(output_);
  for (int w = 0; w < num_words_; w++) {
    os << word_forms_[w] << ' ' << word_assignment_[w] << endl;
  }
  os.close();
}

int main(int argc, char** argv) {
  init_opt(argc, argv);

  readData();
  randomInit();

  if (verbose_ > 0) {
    cerr << "W:     Number of words processed / total" << endl
         << "LL:    Current log-likelihood" << endl
         << "Swaps: Number of words that changed their class" << endl 
         << endl;
  }

  for (int step = 0; num_steps_ < 0 || step < num_steps_; step++) {
    if (verbose_ > 0)
      cerr << "iter: " << step << endl;

    int swaps = update();

    if (swaps == 0) {
      break;
    }

  }

  writeAssignment();
}

