package org.team1540.robot2020;

import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;

public class RamseteConfig {
    public static final double ksVolts = 0.669;
    public static final double kvVoltSecondsPerMeter = 2.76;
    public static final double kaVoltSecondsSquaredPerMeter = 0.662;

//    Ramsete PID controllers
//    public final double kPDriveVel = 19.3;
    public static final double kPDriveVel = 1;
//    public final double kPDriveVel = 0;

    private static final double kTrackwidthMeters = 0.761388065;
    public static final DifferentialDriveKinematics kDriveKinematics =
            new DifferentialDriveKinematics(kTrackwidthMeters);

//    private static final double kWheelCircumference = 0.4863499587;
//    private static final double kEncoderPPR = 512;
//    private static final double encoderMetersPerTick = kWheelCircumference / kEncoderPPR;

    // Motion control
    public static final double kRamseteB = 2;
    public static final double kRamseteZeta = 0.7;

    public static final double kMaxSpeedMetersPerSecond = 2;
    public static final double kMaxAccelerationMetersPerSecondSquared = 1;

    public static final DifferentialDriveVoltageConstraint autoVoltageConstraint =
            new DifferentialDriveVoltageConstraint(
                    new SimpleMotorFeedforward(ksVolts, kvVoltSecondsPerMeter, kaVoltSecondsSquaredPerMeter),
                    kDriveKinematics,
                    10);

    public static TrajectoryConfig trajectoryConfig = new TrajectoryConfig(
            kMaxSpeedMetersPerSecond,
            kMaxAccelerationMetersPerSecondSquared)
            .setKinematics(kDriveKinematics)
            .addConstraint(autoVoltageConstraint);
}
