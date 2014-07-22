
/*******************************************************************/
/*                                                                 */
/*  FILE     fst.C                                                 */
/*  MODULE   fst                                                   */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  basic FST functions                                   */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

namespace SFST {

  using std::vector;
  using std::istream;
  using std::ostream;
  using std::cerr;

  const int BUFFER_SIZE=100000;

  bool Transducer::hopcroft_minimisation=true;


  /*******************************************************************/
  /*                                                                 */
  /*  Arcs::size                                                     */
  /*                                                                 */
  /*******************************************************************/

  int Arcs::size() const

  {
    int n=0;
    for( Arc *p=first_arcp; p; p=p->next ) n++;
    for( Arc *p=first_epsilon_arcp; p; p=p->next ) n++;
    return n;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Arcs::target_node                                              */
  /*                                                                 */
  /*******************************************************************/

  Node *Arcs::target_node( Label l )

  {
    Arc *arc;

    for( arc=first_arcp; arc; arc=arc->next)
      if (arc->label() == l)
	return arc->target_node();
  
    return NULL;
  }

  const Node *Arcs::target_node( Label l ) const

  {
    const Arc *arc;

    for( arc=first_arcp; arc; arc=arc->next)
      if (arc->label() == l)
	return arc->target_node();
  
    return NULL;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::new_node                                           */
  /*                                                                 */
  /*******************************************************************/

  Node *Transducer::new_node()

  {
    Node *node=(Node*)mem.alloc( sizeof(Node) );

    node->init();
    return node;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::new_arc                                            */
  /*                                                                 */
  /*******************************************************************/

  Arc *Transducer::new_arc( Label l, Node *target )

  {
    Arc *arc=(Arc*)mem.alloc( sizeof(Arc) );
    arc->init( l, target);
    return arc;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Arcs::add_arc                                                  */
  /*                                                                 */
  /*******************************************************************/

  void Arcs::add_arc( Label l, Node *node, Transducer *a )

  {
    Arc *arc=a->new_arc( l, node );

    if (l.is_epsilon()) {
      arc->next = first_epsilon_arcp;
      first_epsilon_arcp = arc;
    }
    else {
      arc->next = first_arcp;
      first_arcp = arc;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Arcs::remove_arc                                               */
  /*                                                                 */
  /*******************************************************************/

  int Arcs::remove_arc( Arc *arc )

  {
    Arc **p = (arc->label().is_epsilon()) ? &first_epsilon_arcp : &first_arcp;
    for( ; *p; p=&(*p)->next )
      if (*p == arc) {
	*p = arc->next;
	return 1;
      }
    return 0;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Node::init                                                     */
  /*                                                                 */
  /*******************************************************************/

  void Node::init()

  {
    final = false;
    visited = 0;
    arcsp.init();
    forwardp = NULL;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Node::clear_visited                                            */
  /*                                                                 */
  /*******************************************************************/

  void Node::clear_visited( NodeHashSet &nodeset )

  {
    if (nodeset.find( this ) == nodeset.end()) {
      visited = 0;
      nodeset.insert( this );
      fprintf(stderr," %lu", (unsigned long)nodeset.size());
      for( ArcsIter p(arcs()); p; p++ ) {
	Arc *arc=p;
	arc->target_node()->clear_visited( nodeset );
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::index_nodes                                        */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::index_nodes( Node *node, vector<Node*> *nodearray )

  {
    if (!node->was_visited( vmark )) {
      node->index = (Index)node_count++;
      if (nodearray)
	nodearray->push_back(node);

      for( ArcsIter p(node->arcs()); p; p++ ) {
	Arc *arc=p;
	transition_count++;
	index_nodes( arc->target_node(), nodearray );
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::nodeindexing                                       */
  /*                                                                 */
  /*******************************************************************/

  std::pair<size_t,size_t> Transducer::nodeindexing( vector<Node*> *nodearray )

  {
    if (!indexed) {
      incr_vmark();
      index_nodes( root_node(), nodearray );
      indexed = true;
    }

    return std::pair<size_t,size_t>(node_count, transition_count);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::add_string                                         */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::add_string( char *s, bool extended, Alphabet *a )

  {
    if (a == NULL)
      a = &alphabet;

    Node *node=root_node();
    Label l;
    while (!(l = a->next_label(s, extended)).is_epsilon()) {
      a->insert(l);
      Arcs *arcs=node->arcs();
      node = arcs->target_node( l );
      if (node == NULL) {
	node = new_node();
	arcs->add_arc( l, node, this );
      }
    }
    node->set_final(1);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::Transducer                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer::Transducer( vector<Label> &path )
    : root(), mem()
  {
    Node *node=root_node();

    vmark = 0;
    indexed = false;
    node_count = transition_count = 0;
    deterministic = minimised = true;
    for( size_t i=0; i<path.size(); i++ ) {
      Arcs *arcs=node->arcs();
      node = new_node();
      arcs->add_arc( path[i], node, this );
    }
    node->set_final(1);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::Transducer                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer::Transducer( istream &is, const Alphabet *a, bool verbose, 
			  bool lexcomments  )
    : root(), mem()
  {
    bool extended=false;
    int n=0;
    char buffer[10000];

    vmark = 0;
    indexed = false;
    node_count = transition_count = 0;
    deterministic = true;
    minimised = false;
    if (a) {
      alphabet.copy(*a);
      extended = true;
    }
    while (is.getline(buffer, 10000)) {
      if (verbose && ++n % 10000 == 0) {
	if (n == 10000)
	  cerr << "\n";
	cerr << "\r" << n << " words";
      }

      // delete comments
      if (lexcomments) {
	size_t l = strlen(buffer);
	for( size_t i=0; i<l; i++ )
	  if (buffer[i] == '\\' && buffer[i+1])
	    ; // quoted character
	  else if (buffer[i] == '%') {
	    // comment starts here
	    buffer[i] = 0;
	    break;
	  }
	if (buffer[0] == 0)
	  continue;
      }

      // delete final whitespace characters
      int l;
      for( l=(int)strlen(buffer)-1; l>=0; l-- )
	if ((buffer[l] != ' ' && buffer[l] != '\t' && buffer[l] != '\r') ||
	    (l > 0 && buffer[l-1] == '\\'))
	  break;
      buffer[l+1] = 0;

      add_string(buffer, extended);
    }
    if (verbose && n >= 10000)
      cerr << "\n";
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::Transducer                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer::Transducer( char *s, const Alphabet *a, bool extended )
    : root(), mem()
  {
    vmark = 0;
    indexed = false;
    node_count = transition_count = 0;
    deterministic = minimised = true;
    if (a)
      alphabet.copy(*a);
    add_string(s, extended);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::clear                                              */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::clear()

  {
    vmark = 0;
    deterministic = minimised = false;
    root.init();
    mem.clear();
    alphabet.clear();
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::store_symbols                                      */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::store_symbols(Node *node, SymbolMap &symbol, 
				 LabelSet &labels)
  {
    if (!node->was_visited( vmark )) {
      Arcs *arcs=node->arcs();
      for( ArcsIter p(arcs); p; p++ ) {
	Arc *arc=p;
	Label l=arc->label();

	labels.insert(l);

	Character c = l.upper_char();
	if (symbol.find(c) == symbol.end()) {
	  const char *s = alphabet.code2symbol(c);
	  if (s)
	    symbol[c] = fst_strdup(s);
	}

	c = l.lower_char();
	if (symbol.find(c) == symbol.end()) {
	  const char *s = alphabet.code2symbol(c);
	  if (s)
	    symbol[c] = fst_strdup(s);
	}

	store_symbols( arc->target_node(), symbol, labels );
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::minimise_alphabet                                  */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::minimise_alphabet()

  {
    SymbolMap symbols;
    LabelSet labels;
    incr_vmark();
    store_symbols(root_node(), symbols, labels);
    alphabet.clear();
    for( SymbolMap::iterator it=symbols.begin(); it!=symbols.end(); it++ ) {
      alphabet.add_symbol( it->second, it->first );
      free(it->second);
    }
    for( LabelSet::iterator it=labels.begin(); it!=labels.end(); it++ )
      alphabet.insert(*it);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::rev_det_minimise                                   */
  /*                                                                 */
  /*******************************************************************/

  Transducer &Transducer::rev_det_minimise( bool verbose )

  {
    if (minimised)
      return copy();

    Transducer *a1, *a2;

    a1 = &reverse();
    a2 = &a1->determinise();
    delete a1;

    a1 = &a2->reverse();
    delete a2;

    a2 = &a1->determinise();
    delete a1;

    a2->minimised = true;
    a2->minimise_alphabet();

    return *a2;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::size_node                                          */
  /*                                                                 */
  /*******************************************************************/

  size_t Transducer::size_node( Node *node )

  {
    size_t result = 0;
    if (!node->was_visited( vmark )) {
      result++;
      for( ArcsIter it(node->arcs()); it; it++ ) {
	Arc *arc=it;
	result += size_node( arc->target_node() );
      }
    }
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::size_node                                          */
  /*                                                                 */
  /*******************************************************************/

  size_t Transducer::size()

  {
    incr_vmark();
    return size_node(root_node());
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::enumerate_paths_node                               */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::enumerate_paths_node( Node *node, vector<Label> &path, 
					 NodeHashSet &previous,
					 vector<Transducer*> &result )
  {
    if (node->is_final())
      result.push_back(new Transducer(path));

    for( ArcsIter it(node->arcs()); it; it++ ) {
      Arc *arc=it;

      NodeHashSet::iterator hsit=previous.insert(node).first;
      path.push_back(arc->label());
      enumerate_paths_node( arc->target_node(), path, previous, result );
      path.pop_back();
      previous.erase(hsit);
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::enumerate_paths                                    */
  /*                                                                 */
  /*******************************************************************/

  bool Transducer::enumerate_paths( vector<Transducer*> &result )

  {
    if (is_infinitely_ambiguous())
      return true;
    for( size_t i=0; i<result.size(); i++ )
      delete result[i];
    result.clear();

    vector<Label> path;
    NodeHashSet previous;
    enumerate_paths_node( root_node(), path, previous, result );
    return false;
  }




  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::print_strings_node                                 */
  /*                                                                 */
  /*******************************************************************/

  int Transducer::print_strings_node(Node *node, char *buffer, int pos,
				     FILE *file, bool with_brackets )
  {
    int result = 0;

    if (node->was_visited( vmark )) {
      if (node->forward() != NULL) { // cycle detected
	cerr << "Warning: cyclic analyses (cycle aborted)\n";
	return 0;
      }
      node->set_forward(node);  // used like a flag for loop detection
    }
    if (pos == BUFFER_SIZE)
      throw "Output string in function print_strings_node is too long";
    if (node->is_final()) {
      buffer[pos] = '\0';
      fprintf(file,"%s\n", buffer);
      result = 1;
    }
    for( ArcsIter i(node->arcs()); i; i++ ) {
      int p=pos;
      Arc *arc=i;
      Label l=arc->label();
      alphabet.write_label(l, buffer, &p, with_brackets);
      result |= print_strings_node(arc->target_node(), buffer, p, 
				   file, with_brackets );
    }
    node->set_forward(NULL);

    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::print_strings                                      */
  /*                                                                 */
  /*******************************************************************/

  int Transducer::print_strings( FILE *file, bool with_brackets )

  {
    char buffer[BUFFER_SIZE];
    incr_vmark();
    return print_strings_node( root_node(), buffer, 0, file, with_brackets );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::analyze_string                                     */
  /*                                                                 */
  /*******************************************************************/

  bool Transducer::analyze_string( char *string, FILE *file, bool with_brackets )

  {
    vector<Character> input;
    alphabet.string2symseq( string, input );
    vector<Label> labels;
    for( size_t i=0; i<input.size(); i++ )
      labels.push_back(Label(input[i]));

    Transducer a1(labels);
    Transducer *a2=&(*this || a1);
    Transducer *a3=&(a2->lower_level());
    delete a2;
    a2 = &a3->minimise();
    delete a3;

    a2->alphabet.copy(alphabet);
    bool result = a2->print_strings( file, with_brackets );
    delete a2;
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::generate_string                                    */
  /*                                                                 */
  /*******************************************************************/

  bool Transducer::generate_string( char *string, FILE *file, bool with_brackets)

  {
    Transducer a1(string, &alphabet, false);
    Transducer *a2=&(a1 || *this);
    Transducer *a3=&(a2->upper_level());
    delete a2;
    a2 = &a3->minimise();
    delete a3;

    a2->alphabet.copy(alphabet);
    bool result = a2->print_strings( file, with_brackets );
    delete a2;
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  complete                                                       */
  /*                                                                 */
  /*******************************************************************/

  static void complete( Node *node, Alphabet &alphabet, VType vmark)

  {
    if (node->was_visited( vmark ))
      return;
    for( ArcsIter p(node->arcs()); p; p++ ) {
      Arc *arc=p;
      if (!arc->label().is_epsilon())
	alphabet.insert(arc->label());
      complete(arc->target_node(), alphabet, vmark);
    }  
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::complete_alphabet                                  */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::complete_alphabet()

  {
    incr_vmark();
    complete(root_node(), alphabet, vmark);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  print_node                                                     */
  /*                                                                 */
  /*******************************************************************/

  static void print_node( ostream &s, Node *node, VType vmark, Alphabet &abc )

  {
    if (!node->was_visited( vmark )) {
      Arcs *arcs=node->arcs();
      for( ArcsIter p(arcs); p; p++ ) {
	Arc *arc=p;
	s << node->index << "\t" << arc->target_node()->index;
	s << "\t" << abc.write_char(arc->label().lower_char());
	s << "\t" << abc.write_char(arc->label().upper_char());
	s << "\n";
      }
      if (node->is_final())
	s << node->index << "\n";
      for( ArcsIter p(arcs); p; p++ ) {
	Arc *arc=p;
	print_node( s, arc->target_node(), vmark, abc );
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  operator<<                                                     */
  /*                                                                 */
  /*******************************************************************/

  ostream &operator<<( ostream &s, Transducer &a )

  {
    a.nodeindexing();
    a.incr_vmark();
    print_node( s, a.root_node(), a.vmark,  a.alphabet );
    return s;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  store_node_info                                                */
  /*                                                                 */
  /*******************************************************************/

  static void store_node_info( FILE *file, Node *node )

  {
    // write final flag
    char c=node->is_final();
    fwrite(&c,sizeof(c),1,file);
  
    // write the number of arcs
    int nn = node->arcs()->size();
    if (nn > 65535)
      throw "Error: in function store_node\n";
    unsigned short n=(unsigned short)nn;
    fwrite(&n,sizeof(n),1,file);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  store_arc_label                                                */
  /*                                                                 */
  /*******************************************************************/

  static void store_arc_label( FILE *file, Arc *arc )

  {
    Label l=arc->label();
    Character lc=l.lower_char();
    Character uc=l.upper_char();
    fwrite(&lc,sizeof(lc),1,file);
    fwrite(&uc,sizeof(uc),1,file);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  store_node                                                     */
  /*                                                                 */
  /*******************************************************************/

  static void store_node( FILE *file, Node *node, VType vmark )
  {
    if (!node->was_visited( vmark )) {

      store_node_info( file, node );
  
      // write the arcs
      for( ArcsIter p(node->arcs()); p; p++ ) {
	Arc *arc=p;
	store_arc_label( file, arc );
	unsigned int t = (unsigned int)arc->target_node()->index;
	fwrite(&t,sizeof(t),1,file);
	store_node(file, arc->target_node(), vmark );
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  store_lowmem_node                                              */
  /*                                                                 */
  /*******************************************************************/

  static void store_lowmem_node( FILE *file, Node *node, 
				 vector<unsigned int> &startpos)
  {
    store_node_info( file, node );
  
    // write the arcs
    for( ArcsIter p(node->arcs()); p; p++ ) {
      Arc *arc=p;
      store_arc_label( file, arc );
      unsigned int t=startpos[arc->target_node()->index];
      fwrite(&t,sizeof(t),1,file);
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::store_lowmem                                       */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::store_lowmem( FILE *file )

  {
    fputc('l',file);
    alphabet.store(file);

    // storing size of index table
    vector<Node*> nodearray;
    nodeindexing( &nodearray );

    // compute the start position of the first node
    unsigned int pos=(unsigned int)ftell(file);
    vector<unsigned int> startpos;
    for( size_t i=0; i<nodearray.size(); i++ ) {
      startpos.push_back(pos);
      Node *node=nodearray[i];
      Arcs *arcs=node->arcs();
      pos += (unsigned)(sizeof(char) // size of final flag
			+ sizeof(unsigned short) // size of number of arcs
			+ arcs->size() * (sizeof(Character) * 2 + sizeof(unsigned int))); // size of n arcs
    }

    // storing nodes
    for( size_t i=0; i<nodearray.size(); i++ )
      store_lowmem_node( file, nodearray[i], startpos );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::store                                              */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::store( FILE *file )

  {
    fputc('a',file);

    vector<Node*> nodearray;
    nodeindexing( &nodearray );
    incr_vmark();
    unsigned int n=(unsigned)nodearray.size();
    fwrite(&n,sizeof(n),1,file);
    store_node( file, root_node(), vmark );

    alphabet.store(file);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  read_node                                                      */
  /*                                                                 */
  /*******************************************************************/

  static void read_node( FILE *file, Node *node, Node **p, Transducer *a )
  {
    char c;
    fread(&c,sizeof(c),1,file);
    node->set_final(c);

    unsigned short n;
    fread( &n, sizeof(n), 1, file);

    for( int i=0; i<n; i++ ) {
      Character lc,uc;
      unsigned int t;
      fread(&lc,sizeof(lc),1,file);
      fread(&uc,sizeof(uc),1,file);
      fread(&t,sizeof(t),1,file);
      if (ferror(file))
	throw "Error encountered while reading transducer from file";
      if (p[t])
	node->add_arc( Label(lc,uc), p[t], a );
      else {
	p[t] = a->new_node();
	node->add_arc( Label(lc,uc), p[t], a );
	read_node(file, p[t], p, a );
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::read_transducer_binary                             */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::read_transducer_binary( FILE *file )

  {
    if (fgetc(file) != 'a')
      throw "Error: wrong file format (not a standard transducer)\n";

    vmark = deterministic = 0;
    unsigned int n;
    fread(&n,sizeof(n),1,file); // number of nodes
    if (ferror(file))
      throw "Error encountered while reading transducer from file";

    Node **p=new Node*[n];  // maps indices to nodes
    p[0] = root_node();
    for( unsigned int i=1; i<n; i++)
      p[i] = NULL;
    read_node( file, root_node(), p, this );
    delete[] p;

    alphabet.read(file);

    vmark = 1;
    deterministic = minimised = 1;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  error_message                                                  */
  /*                                                                 */
  /*******************************************************************/

  static void error_message( size_t line )

  {
    static char message[1000];
    sprintf(message, "Error: in line %u of text transducer file", 
	    (unsigned int)line);
    throw message;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::create_node                                        */
  /*                                                                 */
  /*******************************************************************/

  Node *Transducer::create_node( vector<Node*> &node, char *s, size_t line )

  {
    char *p;
    long n = strtol(s, &p, 10);

    if (s == p || n < 0)
      error_message( line );
    if ((long)node.size() <= n)
      node.resize(n+1, NULL);
    if (node[n] == NULL)
      node[n] = new_node(); //new Node;

    return node[n];
  }


  /*******************************************************************/
  /*                                                                 */
  /*  next_string                                                    */
  /*                                                                 */
  /*******************************************************************/

  static char *next_string( char* &s, size_t line )

  {
    // scan the input up to the next tab or newline character
    // and unquote symbols preceded by a backslash
    char *p = s;
    char *q = s;
    while (*q!=0 && *q!='\t' && *q!='\n' && *q!='\r') {
      if (*q == '\\')
	q++;
      *(p++) = *(q++);
    }
    if (p == s)
      error_message(line); // no string found

    char *result=s;
    // skip over following whitespace
    while (*q == ' ' || *q == '\t' || *q == '\n' || *q == '\r')
      q++;
  
    if (*q == 0)
      s = NULL; // end of string was reached
    else
      s = q;  // move the string pointer s

    *p = 0; // mark the end of the result string
  
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::read_transducer_text                               */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::read_transducer_text( FILE *file )

  {
    vector<Node*> nodes;
    nodes.push_back(root_node());

    vmark = deterministic = 0;
    char buffer[10000];
    for( size_t line=0; fgets(buffer, 10000, file ); line++ ) {
      char *p = buffer;
      char *s = next_string(p, line);
      Node *node = create_node( nodes, s, line );
      if (p == NULL)
	node->set_final(true);
      else {
	s = next_string(p, line);
	Node *target = create_node( nodes, s, line );

	s = next_string(p, line);
	Character lc = alphabet.add_symbol(s);
	s = next_string(p, line);
	Character uc = alphabet.add_symbol(s);
	Label l(lc,uc);
	if (l == Label::epsilon)
	  error_message( line );

	alphabet.insert(l);
	node->add_arc( l, target, this );
      }
    }

    vmark = 1;
    deterministic = minimised = 1;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::Transducer                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer::Transducer( FILE *file, bool binary )

  {
    indexed = false;
    node_count = transition_count = 0;
    if (binary)
      read_transducer_binary( file );
    else
      read_transducer_text( file );
  }


  /*  EPSILON REMOVAL ALGORITHM written by Erik Axelson starts here  */

  /*******************************************************************/
  /*                                                                 */
  /*  node_in_copy_tr                                                */
  /*                                                                 */
  /*******************************************************************/

  /* Find the corresponding node in 'copy_tr' for 'node'. If needed, create a new node to 'copy_tr'
     and update 'mapper' accordingly. */
  
  Node *node_in_copy_tr( Node *node, Transducer *copy_tr, map<int, Node*> &mapper ) {
    int node_index = (int)node->index;  // node index in original transducer
    map<int,Node*>::iterator it = mapper.find(node_index); // iterator to associated node in copy_tr
    if (it == mapper.end()) {
      Node *associated_node = copy_tr->new_node(); // create new node in copy_tr
      if (node->is_final())
	associated_node->set_final(true);
      mapper[node_index] = associated_node; // and associate it with node_index
      return associated_node;
    }
    else
      return it->second;
  } 


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::copy_nodes                                         */
  /*                                                                 */
  /*******************************************************************/

  /* Recursive epsilon removal algorithm. Copies arcs and their
     target nodes starting from search_node to node copy_tr_start_node
     in transducer copy_tr. nn and mapper are used to associate nodes
     with nodes in copy_tr. */

  void Transducer::copy_nodes( Node *search_node, Transducer *copy_tr,
			       Node *copy_tr_start_node,
			       map<int, Node*> &mapper ) {

    // go through all arcs leaving from search node
    // (the iterator lists the epsilon arcs first)
    for( ArcsIter it(search_node->arcs()); it; it++ ) {
      Arc arc=*it;

      if (arc.label().is_epsilon()) {
	// 'forward', which is originally NULL, is used as a flag
	// for detecting epsilon transition loops
	if (search_node->forward() != copy_tr_start_node) { 
	  search_node->set_forward(copy_tr_start_node);  // set epsilon flag
	  if (arc.target_node()->is_final())
	    copy_tr_start_node->set_final(true);
	  copy_nodes(arc.target_node(), copy_tr, copy_tr_start_node, mapper);
	  search_node->set_forward(NULL);  // remove epsilon flag
	}
      }

      else {
	// target node in copy_tr
	Node *copy_tr_end_node = 
	  node_in_copy_tr(arc.target_node(), copy_tr, mapper);
	// add arc to copy_tr
	copy_tr_start_node->add_arc( Label(arc.label().lower_char(),
					   arc.label().upper_char()),
				     copy_tr_end_node,
				     copy_tr );
	// if the target node is not visited, copy nodes recursively
	if ( !(arc.target_node()->was_visited(vmark)) )
	  copy_nodes(arc.target_node(), copy_tr, copy_tr_end_node, mapper);
      }

    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::remove_epsilons                                    */
  /*                                                                 */
  /*******************************************************************/

  Transducer &Transducer::remove_epsilons()

  {
    if ( deterministic || minimised )
      return this->copy();

    nodeindexing();
    incr_vmark();
    Transducer *copy_tr = new Transducer();
    copy_tr->alphabet.copy(alphabet);
    map<int, Node*> mapper;
    // mark root node as visited
    root_node()->was_visited(vmark);
    // set copy_tr root node final, if needed
    if (root_node()->is_final())
      copy_tr->root_node()->set_final(true);
    // associate the root_nodes in this and copy_tr 
    // (node indexing for root_node is zero)
    mapper[0] = copy_tr->root_node();

    copy_nodes(root_node(), copy_tr, copy_tr->root_node(), mapper);
    incr_vmark();	

    return *copy_tr;
  }

  // EPSILON REMOVAL ALGORITHM ENDS

}
