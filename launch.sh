#!/bin/sh
clear
# полный путь до скрипта
ABSOLUTE_FILENAME=`readlink -e "$0"`
# каталог в котором лежит скрипт
DIRECTORY=`dirname "$ABSOLUTE_FILENAME"`

value="java -jar `cat $DIRECTORY/vm.options` $DIRECTORY/ChaosEnglishWordsTeacher.jar"
echo $value
#$value
echo `$value`

