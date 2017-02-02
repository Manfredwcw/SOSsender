#!/usr/bin/python
import socket
import sys

s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
host = socket.gethostname()
port = 9995

s.connect((host,port))

msg = s.recv(1024)
print msg.decode('utf-8')

while 1:
	msg = s.recv(1024)
	message = raw_input('input message:(input "exit" to terminate):\n')
	if message == 'exit' or msg =='exit':
		break
	try:
		s.send(message.encode('utf-8'))
	except socket.error:
		print 'send failed!'
		sys.exit()

	print 'send success!'

s.send('exit'.encode('utf-8'))
s.close()
print 'connect end!'