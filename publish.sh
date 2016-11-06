#!/bin/bash


tomcat_dir=/usr/local/apache-tomcat-8.5.6
app_name=ifs

## 判断是否执行成功
check_result() {
    if  [ "$1" != "0" ] ; then
        echo "execute error"
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
rm -rf ${tomcat_dir}/webapps/${app_name}*
mv ./target/${app_name}.war ${tomcat_dir}/webapps/

check_result $?

start_time=`date +%s`

## 重启tomcat
echo "--> ${tomcat_dir} shutdown.sh"
${tomcat_dir}/bin/shutdown.sh
echo "--> ${tomcat_dir} startup.sh"
${tomcat_dir}/bin/startup.sh

end_time=`date +%s`
sum=$(( $end_time-$start_time ))
echo "total consuming $sum ms"

jps