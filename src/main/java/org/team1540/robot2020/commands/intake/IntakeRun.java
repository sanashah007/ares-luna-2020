package org.team1540.robot2020.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeRun extends CommandBase {
    private Intake intake;
    private double targetRPM;

    public IntakeRun(Intake intake, double targetRPM) {
        this.intake = intake;
        this.targetRPM = targetRPM;
        addRequirements(intake);

    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        intake.setVelocity(targetRPM);
    }

    @Override
    public void end(boolean interrupted) {
        intake.setPercent(0);
    }
}
