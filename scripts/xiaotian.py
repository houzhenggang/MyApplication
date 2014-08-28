#!/usr/bin/env python
# _*_ coding:utf-8 _*_
import sys
import os

START_STRING = "GP_COMMAD_PA"
START_STRING2 = "SPI_WriteData"

allLine1 = []

def toPars(line):
    temp = line.split(";")
    tempLine = []
    for one in temp:
        if one.startswith(START_STRING):
            indexS = one.find("(")
            indexE = one.rfind(")")
            text = one[indexS + 1:indexE]
            tempLine.append(text)
        elif one.startswith(START_STRING2):
            indexS = one.find("(")
            indexE = one.rfind(")")
            text = one[indexS + 1:indexE]
            tempLine.append(text)
    allLine1.append(tempLine)

def toFile():
    newFile = open("newFile.txt", "w")
    index2 = 1
    for line in allLine1:
        index = 0
        number = 0
        head = ""
        other = []
        for line2 in line:
            if index == 0:
                number = line2
            elif index == 1:
                head = line2
            else:
                other.append(line2)
            index+=1
        number = int(number) - 1
        newLine = "{" + head
        if number == 0:
            newLine += "},"
        else:
            newLine += "," + str(number) + ",{" 
            index = 0
            for two in other:
                newLine += two
                if index != len(other) - 1:
                    newLine += ","
                index += 1
            newLine += "}},"
        print newLine
        trip = "\n"
        if index2 % 2 == 0:
            trip += "\n"
        newFile.write(newLine + trip)
        index2 += 1
    newFile.close()

if __name__ == '__main__':
    if len(sys.argv) == 2:
        fileName = sys.argv[1]
        if os.path.exists(fileName):
            lines = open(fileName, 'r')
            for line in lines:
                line = line.lstrip()
                if line.startswith(START_STRING):
                    toPars(line)
            toFile()
        else:
            print "文件不存在！"
    else:
        print "请输入文件路径"
