package edu.und.cs.com.heart_monitor;

public class AnomalyDetection {

    String AnomaliesDetect = "";
    String FileName;
    int heartrate = 80;

    // Add if statement for each abnormality
    // if detected, append upto AnomaliesDetected string
    public String Detect(String fileName) {
        this.FileName = fileName;
        if (Tachycardia()) {
            AnomaliesDetect += "Tachycardia detected;";
        } else if (BradyCardia()) {
            AnomaliesDetect += "Bradycardia detected;";
        }

        if (AnomaliesDetect.equals("")) {
            return "No anomalies detected";
        } else {
            return AnomaliesDetect;
        }
    }

    private boolean Tachycardia() {
        if (heartrate > 120) {
            return true;
        }
        return false;
    }

    private boolean BradyCardia() {
        if (heartrate < 80) {
            return true;
        }
        return false;
    }
}
