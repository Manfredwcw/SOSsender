
from __future__ import division
from textmagic.rest import TextmagicRestClient
from PyQt4 import QtCore, QtGui, uic
import webbrowser
import threading
import socket
import time
import sys

qtCreatorFile = "Krankenhaus.ui"
Ui_MainWindow, QtBaseClass = uic.loadUiType(qtCreatorFile)

class MyApp(QtGui.QMainWindow, Ui_MainWindow):
	def __init__(self):
		QtGui.QMainWindow.__init__(self)
		Ui_MainWindow.__init__(self)
		self.setupUi(self)

		self.tel_no = []
		self.tel_tmp = ""
		self.compare = ""
		self.cSocket = ""
		self.latitude = ''
		self.longitude = ''
		self.flag = 1
		self.min = 0
		self.max = 200
		self.rate = ""
		self.messages = ""

		self.timer = QtCore.QTimer(self)
		self.Button_DetektStarten.clicked.connect(self.starten)
		self.Button_DetektStarten.clicked.connect(self.showinfo)
		self.Button_DetektBeenden.clicked.connect(self.detektbeenden)
		self.Button_openmap.clicked.connect(self.openmaps)
		#self.Button_add_tel.clicked.connect(self.addtel)
		#self.Button_del_tel.clicked.connect(self.deltel)
		recvThread = threading.Thread(target = self.recvFromClient)
		recvThread.setDaemon(True)
		recvThread.start()		
	
	def starten(self):
		self.timer.timeout.connect(self.showRate)
		self.timer.start(1000)
	

	def showinfo(self):
		self.photo_label.setPixmap(QtGui.QPixmap("photo.jpg"))
		self.lineEdit_name.setText('Alex')
		self.lineEdit_sex.setText('Male')
		self.lineEdit_age.setText('65')
		self.lineEdit_disease.setText('Herzinfarkt')
		self.lineEdit_tel.setText('+4915758622065')
		self.lineEdit_address.setText('xxx Str.15,Hannover')

	#def addtel(self):
	#	pass

	#def deltel(self):
	#	pass
		
	def openmaps(self):
		if(self.latitude =='' or self.longitude == ''):
			pass
		else:
			url = 'https://www.google.de/maps/place/'
			webbrowser.open(url+self.latitude+'+'+self.longitude)

	def recvFromClient(self):
		ssocket,addr = serversocket.accept()
		self.cSocket = ssocket
		#msg = 'connect success!'
		#self.cSocket.send(msg.encode('utf-8'))
		while 1:
			try:
				data = ssocket.recv(1024)
				data1 = data.encode('utf-8')
				#main logic here to check datas from phone
				self.logic(data1)
			except:
				return
	
	def showRate(self):
		#self.cSocket.send(self.flag.encode('utf-8'))
		if self.rate != self.compare:
			self.compare = self.rate
			self.HerzschlagDaten.append(self.compare)	
		else:
			return
		
	def socketexit(self):
		self.flag = 0
		self.HerzschlagDaten.append("========Exit success!=======")                      
		ssocket.close()

	def detektbeenden(self):
		self.socketexit()

	def send_sms(self):
		username = "chengweiwang"
		token = "mKGVo2k0jO6fKpRrWZdu0xMEzkat7S"
		phonenum = "+4915758622065"
		txt = "I need your help"

		client = TextmagicRestClient(username,token)
		sms = client.messages.create(phones=phonenum,text=txt)

	def logic(self,datas):
		self.datas = datas
		if datas == 'exit':
			self.socketexit()
		if datas[0:8] == 'location':
			#add longitude and latitude data on UI
			for i in xrange(len(datas)):
				if datas[i] == '*':
					self.latitude = datas[9:i]
					self.longitude = datas[i+1:-1]
					self.lineEdit_latitude.setText(self.latitude)
					self.lineEdit_longitude.setText(self.longitude)
					self.HerzschlagDaten.append("============GPS!============")
		if datas[0:5] == 'hilfe':
			self.send_sms()
		if datas[0:4] == 'Rate':
			self.rate = datas
		

if __name__ == '__main__':
	serversocket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
	host = socket.gethostname()
	port = 5000
	serversocket.bind((host,port))
	serversocket.listen(5)

	app = QtGui.QApplication(sys.argv)
	window = MyApp()
	window.show()
	sys.exit(app.exec_())

	

