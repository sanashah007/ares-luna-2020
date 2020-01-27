package org.team1540.robot2020.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Indexer extends SubsystemBase {
    private CANSparkMax cord = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
    private CANEncoder cordEncoder = cord.getEncoder();

    private AnalogInput stagingSensor = new AnalogInput(0);
    private AnalogInput topSensor = new AnalogInput(1);

    private int balls = 0;

    public Indexer() {
        cord.setIdleMode(CANSparkMax.IdleMode.kBrake);
    }

    public void setPercent(double speed) {
        cord.set(speed);
    }

    public boolean getStagingSensor() {
        return stagingSensor.getValue() >= (stagingSensor.getVoltage() / 2);
    }

    public boolean getTopSensor() {
        return topSensor.getValue() >= (topSensor.getVoltage() / 2);
    }

    public double getEncoderInches() {
        return cordEncoder.getPosition();
    }

    public void resetEncoder() {
        cordEncoder.setPosition(0);
    }

    public int getBalls() {
        return balls;
    }

    public boolean isFull() {
        return getBalls() == 5;
    }

    public void addBall() {
        balls++;
    }

    public void removeBall() {
        balls--;
    }
}
