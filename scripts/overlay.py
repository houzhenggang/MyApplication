#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os

projects_list = []
current_project = ""
overlay_path_base = "vendor/overlay_project/"
overlay_files = []

def gen_project_name():
    files = open("projects_list.txt", "r")
    for line in files:
        projects_list.append(line.rstrip())

def overlay_file_test():
    overlay_path = overlay_path_base + current_project
    if os.path.exists(overlay_path):
        get_all_files(overlay_path)
    do_overlay()

def do_overlay():
    print overlay_files
    overlay_path = overlay_path_base + current_project
    for f in overlay_files:
        copy_src = overlay_path + "/"
        copy_tar = f.replace(copy_src, "");
        cmd = "cp " + f + " " + copy_tar
        print cmd
        os.system(cmd)

def get_all_files(dir):
    lists = os.listdir(dir)
    abspath = os.path.abspath(dir)
    for f in lists:
        if os.path.isfile(abspath + "/" + f):
            overlay_files.append(abspath + "/" + f)
        else:
            get_all_files(abspath + "/" + f)

if __name__=='__main__':
    gen_project_name()
    if len(sys.argv) == 2:
        current_project = sys.argv[1]
        if current_project not in projects_list:
            print "---------项目不存在-------"
        else:
            overlay_file_test()
    else:
        print "------------请在命令后加上项目名-----------"
        for pro in projects_list:
            print "-------" + pro
