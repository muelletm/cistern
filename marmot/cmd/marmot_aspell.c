#include<aspell.h>

#include<stdio.h>
#include<string.h>

#define BUFFER_SIZE 1000

int main(int argc, char** argv) {

  if (argc != 3) {
    fprintf(stderr, "Error: Usage marmot_aspell <lang> <encoding>\n");
    return 1;
  }

  AspellConfig * spell_config = new_aspell_config();
  aspell_config_replace(spell_config, "lang", argv[1]);
  aspell_config_replace(spell_config, "encoding", argv[2]);
  aspell_config_replace(spell_config, "ignore", "0");
  aspell_config_replace(spell_config, "ignore-case", "false");
  aspell_config_replace(spell_config, "ignore-accents", "false");

  AspellCanHaveError * possible_err = new_aspell_speller(spell_config);
  
  if (aspell_error_number(possible_err) != 0) {
    fprintf(stderr, "Error: %s\n", aspell_error_message(possible_err));
    return 2;
  }
  
  AspellSpeller * spell_checker = to_aspell_speller(possible_err);

  FILE *infile = stdin;
  FILE *outfile = stdout;

  char buffer[BUFFER_SIZE];
  while (fgets(buffer, BUFFER_SIZE, infile)) {
    int length =(int) strlen(buffer) - 1;
    if (buffer[length] == '\n') {
       buffer[length] = '\0';
    }
    int correct = aspell_speller_check(spell_checker, buffer, -1);
    fprintf(outfile, "%d\n", correct);
    fflush(outfile);
  }

  delete_aspell_speller(spell_checker);
  delete_aspell_config(spell_config);     
  return 0;
}
