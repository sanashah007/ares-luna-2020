package org.team1540.robot2020.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.team1540.robot2020.subsystems.Shooter;

public class SpinUpShooter extends CommandBase {

    private final int targetVelocityTicksPerDecisecond;
    private Shooter shooter;

    public SpinUpShooter(Shooter shooter, int targetVelocityTicksPerDecisecond) {
        this.shooter = shooter;
        this.targetVelocityTicksPerDecisecond = targetVelocityTicksPerDecisecond;
    }

    @Override
    public void initialize() {
        shooter.setVelocityTicksPerDecisecond(targetVelocityTicksPerDecisecond);
    }

    @Override
    public boolean isFinished() {
        return Math.abs(shooter.getVelocityTicksPerDecisecond() - targetVelocityTicksPerDecisecond) <= Shooter.shooterSpeedTolerance;
    }
}

// TODO: keep motor spinning while actually shooting