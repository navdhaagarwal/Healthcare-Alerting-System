import os
import json
import re
import csv

data_dir = "dataset/set-a"
all_records_dict={}

set_a_file=open("set_a_RespRate_file.csv","w")

data=["PatientID,Timestamp,Metric,Value\n"]

for root, dirs, files in os.walk(data_dir):
    for file in files:
        if file.endswith('.txt'):
            patient_timestamps=[]
            pateint_data={}
            t1 = 0
            record_name = os.path.splitext(file)[0]
            record_data=open(data_dir+"/"+file)
            for line in record_data:
                if "00:00," in line:
                    continue
                elif "Time," in line:
                    continue
                elif ",RespRate," in line:
                    if t1==0:
                        t1=line.split(",")[0].replace(":",".")
                    timestamp=str(round(float(line.split(",")[0].replace(":","."))- float(t1),0))
                    patient_timestamps.append(timestamp)
                    pateint_data[timestamp]=record_name+","+timestamp+","+line.split(",")[1]+","+line.split(",")[2]

            one_data=[]
            for x in range(40):
                if str(float(x)) in pateint_data.keys():
                    one_data.append(pateint_data[str(float(x))])
                else:
                    one_data = []
                    break
            # print(one_data)
            data.extend(one_data)

            if one_data!=[]:
                print(record_name)


for i in data:
    set_a_file.write(i)
