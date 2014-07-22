
/*******************************************************************/
/*                                                                 */
/*     File: Socket.h                                              */
/*   Author: Helmut Schmid                                         */
/*  Purpose:                                                       */
/*  Created: Fri Aug 15 14:19:19 2008                              */
/* Modified: Wed Sep 29 08:44:43 2010 (schmid)                     */
/*                                                                 */
/*******************************************************************/


namespace SFST {

  /*****************  class Socket  **********************************/

  class Socket {
  
    int portno;  /* port address */
    int sockfd;
    struct sockaddr_in serv_addr;
    struct sockaddr cli_addr;
    socklen_t clilen;
  
  public:
    int next_client() {
      return accept( sockfd, (struct sockaddr *)&serv_addr, &clilen);
    }

  Socket( int port=7070 ): portno( port ) {
      /* create a socket */
      sockfd = socket(AF_INET, SOCK_STREAM, 0);
      if (sockfd < 0) {
	fprintf(stderr, "ERROR opening socket\n");
	exit(1);
      }
    
      /* initialise serv_addr with zeros */
      bzero((char *) &serv_addr, sizeof(serv_addr));
      serv_addr.sin_family = AF_INET;
    
      /* convert portno to network byte order and */
      /* store it in serv_addr.sin_port */
      serv_addr.sin_port = htons(portno);
    
      /* set the host IP address (available in INADDR_ANY) */
      serv_addr.sin_addr.s_addr = INADDR_ANY;
    
      /* bind the socket to a host and port */
      if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
	fprintf(stderr, "ERROR on binding\n");
	exit(1);
      }
    
      /* Listen to the socket; up to 5 connections at a time */
      listen(sockfd, 5);
      clilen = sizeof(cli_addr);
    }
  };

}
