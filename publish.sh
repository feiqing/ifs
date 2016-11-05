#!/bin/bash

## 判断是否执行成功
check_result() {
    if  [ "$1" != "0" ] ; then
        exit -1
    fi
}

## 拉取最新代码
git pull

check_result $?

## 重新打包
rm -rf target
mvn package -Dmaven.test.skip=true

check_result $?

## 拷贝war包到目标目录
rm -rf /usr/local/apache-tomcat-8.5.6/webapps/IFS*
mv ./target/IFS.war /usr/local/apache-tomcat-8.5.6/webapps/

check_result $?

start_time=`date +%s`

## 重启tomcat
cd /usr/local/apache-tomcat-8.5.6/bin/
echo "--> shutdown.sh"
./shutdown.sh
echo "--> startup.sh"
./startup.sh

end_time=`date +%s`
sum=$(( $end_time-$start_time ))
echo "total consuming $sum ms"

jps