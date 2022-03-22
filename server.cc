// Server side C/C++ program to demonstrate Socket programming
#include <iostream>
#include <unistd.h>
#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>
#include <fcntl.h>
#include <algorithm>
#define PORT 8080

int main(int argc, char const *argv[])
{
	int server_fd, new_socket, valread;
	struct sockaddr_in address;
	int opt = 1;
	int addrlen = sizeof(address);
	char buffer[1024] = {0};

	const char *ACK = "ACKDAT\n";

	std::string data;

	socklen_t len;
	char *error;
	
	// Creating socket file descriptor
	if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
	{
		perror("socket failed");
		exit(EXIT_FAILURE);
	}
	
	// Forcefully attaching socket to the port 8080
	if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT,
												&opt, sizeof(opt)))
	{
		perror("setsockopt");
		exit(EXIT_FAILURE);
	}
	address.sin_family = AF_INET;
	address.sin_addr.s_addr = INADDR_ANY;
	address.sin_port = htons( PORT );
	
	// Forcefully attaching socket to the port 8080
	if (bind(server_fd, (struct sockaddr *)&address,
								sizeof(address))<0)
	{
		perror("bind failed");
		exit(EXIT_FAILURE);
	}
	if (listen(server_fd, 3) < 0)
	{
		perror("listen");
		exit(EXIT_FAILURE);
	}
	if ((new_socket = accept(server_fd, (struct sockaddr *)&address,
					(socklen_t*)&addrlen))<0)
	{
		perror("accept");
		exit(EXIT_FAILURE);
	}

	//int retval = getsockopt (new_socket, SOL_SOCKET, SO_ERROR, &error, &len);
	fcntl(new_socket, F_SETFL, O_NONBLOCK);

	while(true){
		valread = read( new_socket , buffer, 1024);
		for(int i = 0;i < 1024 ; i++){
			if(buffer[i]!=0){
				data = data + buffer[i];
			}
			buffer[i]=0;
		}
		if(!data.empty()){
			//remove /n from data
			data.erase(std::remove(data.begin(), data.end(), '\n'), data.end());
			std::cout<<"#"<<data<<"#"<<std::endl;
			send(new_socket , ACK , strlen(ACK) , 0 );
		}
		data.clear();
	}
	return 0;
}

