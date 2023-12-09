import pandas as pd

file1 = pd.read_csv("HR_interpolated_data.csv")

file2 = pd.read_csv("RespRate_interpolated_data.csv")

merged_data = pd.merge(file1, file2, on=['PatientID', 'Timestamp'], how='outer')

merged_data = merged_data.dropna()

merged_data = merged_data.rename(columns={'Value_x': 'HR', 'Value_y': 'RespRate'})

result = merged_data[['PatientID', 'Timestamp', 'HR', 'RespRate']]

result.to_csv("combined_data.csv", index=False)
