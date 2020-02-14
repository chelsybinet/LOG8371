import os
import sys
import subprocess

dirs = os.listdir()
folders = list(filter(lambda x: os.path.isdir(x), dirs) )
for index,directory in enumerate(folders):
    print(f"Building modules {index + 1} out of {len(folders)}.")
    print(f"../gradlew :modules:{directory}:assemble")
    child = subprocess.Popen(["../gradlew", f":modules:{directory}:assemble"], stdout=subprocess.PIPE)
    streamdata = child.communicate()[0]
    rc = child.returncode
    if(rc != 0):
        sys.exit(1)

