import pandas as pd
import numpy as np

data = pd.read_csv("set_a_RespRate_file.csv")

new_time = np.arange(data['Timestamp'].min(), data['Timestamp'].max() + 0.01, 0.01)

interpolated_data = pd.DataFrame(columns=['PatientID', 'Timestamp', 'Metric', 'Value'])

for (patient_id, metric), group in data.groupby(['PatientID', 'Metric']):
    interpolated_values = np.interp(new_time, group['Timestamp'], group['Value'])

    interpolated_values = np.round(interpolated_values, 0)
    new_time_rounded = np.round(new_time, 2)

    new_rows = pd.DataFrame({
        'PatientID': patient_id,
        'Timestamp': new_time_rounded,
        'Metric': metric,
        'Value': interpolated_values
    })

    interpolated_data = pd.concat([interpolated_data, new_rows])

interpolated_data.to_csv("RespRate_interpolated_data.csv", index=False)
