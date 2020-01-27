package org.team1540.robot2020.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {
    private TalonFX climberA = new TalonFX(12);

    private Servo ratchet = new Servo(0);

    public Climber() {
        setRatchet(true);
    }

    public void setPercent(double percent) {
        climberA.set(ControlMode.PercentOutput, percent);
    }

    public double getPosition() {
        return climberA.getSelectedSensorPosition();
    }

    public void setRatchet(Boolean on) {
        ratchet.set(on ? 1 : 0);
    }
}
