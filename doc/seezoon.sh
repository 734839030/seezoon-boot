#!/bin/bash

ENV=uat

JAVA_OPTS="-Xmx512m -Xms128m -XX:+UseG1GC -verbose:gc -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC -XX:+PrintGCDateStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M  -Xloggc:./logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/dump"
SERVER_OTPS="--server.port=8081 --server.connection-timeout.seconds=10 --server.tomcat.max-connections=2000 --server.tomcat.accept-count=1000 --server.tomcat.max-threads=2000"
PROJECT_PATH=$(cd `dirname $0`; pwd)
cd $PROJECT_PATH
APP_NAME="${PROJECT_PATH##*/}"
APP_JAR_FILE=$APP_NAME".jar"
APP_LOG_PATH=./logs/seezoon.log
SHELL_PWD=$PROJECT_PATH
pid=0

SOURCE_CODE_PATH=/usr/local/project-build/${APP_NAME}/source-code
NGINX_STATIC_PATH=/usr/local/nginx/html/${APP_NAME}/


checkpid(){
	pid=`ps -ef |grep $APP_JAR_FILE |grep -v grep |awk '{print $2}'` 
}
start(){
	checkpid
	if [ ! -n "$pid" ]; then  
		if [ ! -d logs  ];
	  		then mkdir logs
		fi
		
	    nohup java -jar $JAVA_OPTS $APP_JAR_FILE --spring.profiles.active=$ENV $SERVER_OTPS > $APP_LOG_PATH 2>&1 &  
	    echo "-------------starting --------------------"  
	    echo "-------------stared ctrl + c to exit   --------------------"  
	    tail -f $APP_LOG_PATH  
	 else  
	      echo "$APP_NAME is already running PID: $pid"     
  	fi  
}

stop(){  
    checkpid  
    if [ ! -n "$pid" ]; then  
     echo "$APP_NAME not running"  
    else  
      echo "$APP_NAME stop..."  
      kill $pid  
      sleep 5
      checkpid
      if [ -n "$pid" ] 
      	then  kill -9 $pid  
      fi
      echo "$APP_NAME stoped !!!"  
    fi   
}  
status(){  
   checkpid  
   if [ ! -n "$pid" ]; then  
     echo "$APP_NAME not running"  
   else  
     echo "$APP_NAME running PID: $pid"  
   fi   
}  

restart(){  
    stop   
    sleep 1s  
    start  
}  

deploy_java(){
	cd ${SOURCE_CODE_PATH}/${APP_NAME}
	mvn clean package
	cd $SHELL_PWD
	stop 
	mv -f ${SOURCE_CODE_PATH}/${APP_NAME}/target/${APP_JAR_FILE} $SHELL_PWD
	start
}
deploy_static(){
	cd ${SOURCE_CODE_PATH}/${APP_NAME}/static
	gulp $ENV
	\cp -R dist/* ${NGINX_STATIC_PATH}
	echo "-------------deploy static comleted--------------------"
}

deploy(){
 	echo "----------------------------------"  
	echo "please enter your choise:" 
	echo "(1) 后台资源"
	echo "(2) 前端静态资源"
	echo "(3) 全部部署"
	echo "(0) Exit Menu"
	echo "----------------------------------"
	
	# 首次部署时候需要检出代码
	# git clone xxx.git
	# svn checkout xxxx --username huangdengfeng
	
	read input
	cd ${SOURCE_CODE_PATH}/${APP_NAME}
	git reset --hard
	git pull
	
	# svn 命令
	# svn update
	
	case $input in
	    1)
	    deploy_java
	    exit;;
	    2)
	    deploy_static
	    exit;;
	    3)
	    deploy_static
	    deploy_java
	    exit;;
	    0)
	    exit;;
	    *)
	    echo "----输入不符合预期，部署终止 ----"
	    exit;;
    esac
}
log(){
	less $APP_LOG_PATH
}
case $1 in    
      start) start;;    
      stop)  stop;;   
      restart)  restart;;    
      status)  status;;     
      deploy) deploy;;
      log) log;;
      *)  echo "require start|stop|restart|status|deploy|log" ;;    
esac  
