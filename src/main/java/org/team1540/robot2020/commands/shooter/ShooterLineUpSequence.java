package org.team1540.robot2020.commands.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpiutil.math.MathUtil;
import org.team1540.robot2020.LocalizationManager;
import org.team1540.robot2020.commands.drivetrain.DriveTrain;
import org.team1540.robot2020.commands.drivetrain.PointToTarget;
import org.team1540.robot2020.commands.hood.Hood;
import org.team1540.robot2020.commands.hood.HoodSetPositionContinuous;
import org.team1540.robot2020.utils.ChickenXboxController;
import org.team1540.robot2020.utils.LookupTableUtils;

public class ShooterLineUpSequence extends ParallelCommandGroup {
    private LocalizationManager localization;
    private PointToTarget pointingCommand;
    private ShooterSetVelocityContinuous shootingCommand;
    private HoodSetPositionContinuous hoodCommand;

    private double[] HOOD = new double[]{-71.78778076171875, -77.28832244873047, -95.57478332519531, -105.16877746582031, -139.78330993652344, -224.01043701171875};
    private double[] DISTANCE = new double[]{352.06299212598424, 261.11811023622045, 183.16535433070865, 154.03149606299212, 120.96062992125984, 63.874015748031496};
    private double[] FLYWHEEL = new double[]{5000.0, 5000.0, 5000.0, 2870.0, 2480.0, 1780.0};

    private double lastLidarDistance = 200;

    public ShooterLineUpSequence(DriveTrain driveTrain, Shooter shooter, Hood hood, ChickenXboxController driverController, LocalizationManager localization) {
        this.localization = localization;

        // TODO: fix D constant for flywheel
        this.pointingCommand = new PointToTarget(localization.getNavX(), driveTrain, driverController, localization.getLimelight());
        this.shootingCommand = new ShooterSetVelocityContinuous(shooter, () -> MathUtil.clamp(LookupTableUtils.getDoubleLookupTable(lastLidarDistance, DISTANCE, FLYWHEEL), 1000, 5800));
        this.hoodCommand = new HoodSetPositionContinuous(hood, () -> MathUtil.clamp(LookupTableUtils.getDoubleLookupTable(lastLidarDistance, DISTANCE, HOOD), -230, -1));

        addCommands(
                pointingCommand,
                shootingCommand,
                hoodCommand
        );
    }

    @Override
    public void execute() {
        super.execute();

        if (pointingCommand.hasReachedGoal()) {
            lastLidarDistance = localization.getLidar().getDistance();
        }

        SmartDashboard.putNumber("ShooterLineUpSequence/lastLidarDistance", lastLidarDistance);
    }

    public boolean isLinedUp() {
        if (!isScheduled()) return false;
        return pointingCommand.hasReachedGoal() && shootingCommand.hasReachedGoal() && hoodCommand.hasReachedGoal();
    }

}
