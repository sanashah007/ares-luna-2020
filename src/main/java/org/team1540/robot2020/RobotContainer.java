package org.team1540.robot2020;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import org.apache.log4j.Logger;
import org.team1540.robot2020.commands.climber.Climber;
import org.team1540.robot2020.commands.climber.ClimberSequence;
import org.team1540.robot2020.commands.drivetrain.DriveTrain;
import org.team1540.robot2020.commands.drivetrain.PointDrive;
import org.team1540.robot2020.commands.funnel.Funnel;
import org.team1540.robot2020.commands.hood.Hood;
import org.team1540.robot2020.commands.hood.HoodManualControl;
import org.team1540.robot2020.commands.indexer.Indexer;
import org.team1540.robot2020.commands.indexer.IndexerBallQueueSequence;
import org.team1540.robot2020.commands.indexer.IndexerPercent;
import org.team1540.robot2020.commands.intake.Intake;
import org.team1540.robot2020.commands.intake.IntakeRun;
import org.team1540.robot2020.commands.shooter.Shooter;
import org.team1540.robot2020.commands.shooter.ShooterLineUpSequence;
import org.team1540.robot2020.utils.ChickenXboxController;
import org.team1540.robot2020.utils.InstCommand;
import org.team1540.robot2020.utils.NatesPolynomialRegression;

import static org.team1540.robot2020.utils.ChickenXboxController.XboxButton.*;


public class RobotContainer {

    // TODO: logging debugMode variable to avoid putting things to networktables unnecessarily
    // TODO: don't use SmartDashboard, just use the network tables interface

    private static final boolean TEST_MODE = false;

    private static final Logger logger = Logger.getLogger(RobotContainer.class);

    private ChickenXboxController driverController = new ChickenXboxController(0);
    private ChickenXboxController copilotController = new ChickenXboxController(1);
    private ChickenXboxController testingController = new ChickenXboxController(2);
//    private ChickenXboxController testClimbController = new ChickenXboxController(2);
//    private ChickenXboxController testControlPanelController = new ChickenXboxController(3);

    private LocalizationManager localizationManager = new LocalizationManager();
    private NatesPolynomialRegression regression = new NatesPolynomialRegression(3);

    private DriveTrain driveTrain = new DriveTrain(localizationManager.getNavX());
    private Intake intake = new Intake();
    private Funnel funnel = new Funnel();
    private Indexer indexer = new Indexer();
    private Shooter shooter = new Shooter();
    private Hood hood = new Hood();
    //    private ControlPanel controlPanel = new ControlPanel();
    private Climber climber = new Climber();

    RobotContainer() {
        logger.info("Creating robot container...");

        initButtonBindings();
        initModeTransitionBindings();
        initDefaultCommands();
        initDashboard();


        new LocalizationManager().schedule();
//        new FunctionalCommand(() -> {
//        }, () -> localizationManager.periodic(), (interrupted) -> {
//        }, () -> false).schedule();
    }

    @SuppressWarnings("DanglingJavadoc")
    private void initButtonBindings() {
        logger.info("Initializing button bindings...");

        /**************************************************************************************************
         *                                                                                                *
         *               -= Driver =-                                        -= COPILOT =-                *
         *                                                                                                *
         *            ↓------------------------- Shooter Lineup            ↓-------------------------- Climber
         *         BUMPER            BUMPER <--- Run Indexer            BUMPER            BUMPER          *
         *       /--------\       /---------\                         /--------\       /---------\        *
         *     /    (+)   '-----'      Y     \                      /    (+)   '-----'    Y       \       *
         *    /            o O o    X     B   \                   /             o O o   X    B <--\--- Stop intake
         *   /      ↑                  A      |                  /      ↑       ^   ^     A <-----|--- Start intake
         *  |     ← + →         (+)           |                 |    ← + →      '---'--(+)--------|--- Climber
         *  |       ↓    _________            |                 |      ↓     _________            |       *
         *  |          /          \           |                 |          /          \           |       *
         *  |         /            \          |                 |         /            \          |       *
         *  \________/              \________/                  \________/              \________/        *
         *                                                                                                *
         **************************************************************************************************/

        // Driver
        copilotController.getButton(LEFT_BUMPER).whileHeld(new ShooterLineUpSequence(driveTrain, shooter, hood, driverController, localizationManager));
        copilotController.getButton(RIGHT_BUMPER).whileHeld(new IndexerPercent(indexer, 1.0));

        // Copilot
        Command ballQueueCommand = new IndexerBallQueueSequence(indexer, funnel);
        Command intakeCommand = new IntakeRun(intake).alongWith(new ScheduleCommand(ballQueueCommand));
        copilotController.getButton(A).whenPressed(intakeCommand);
        copilotController.getButton(B).cancelWhenPressed(intakeCommand);
        copilotController.getButton(X).whenPressed(new InstantCommand(() -> funnel.stop(), funnel));

//        ShooterSequence(intake, funnel, indexer, shooter, hood)
        ClimberSequence climberSequence = new ClimberSequence(climber, copilotController.getAxis(ChickenXboxController.XboxAxis.LEFT_TRIG));
        copilotController.getButton(BACK).and(copilotController.getButton(START)).whenActive(() -> {
            if (climberSequence.isScheduled()) {
                climberSequence.cancel();
            }
            climberSequence.schedule();
        });


        // Testing
//        testingController.getButton(LEFT_BUMPER).whileHeld();

//        testingController.getButton(B).whenPressed(new IndexerManualControl(indexer,
//                testingController.getAxis(ChickenXboxController.XboxAxis.LEFT_Y).withDeadzone(0.1)));
//
//        testingController.getButton(Y).whenPressed(new HoodZeroSequence(hood));
//
//        // Regression
//        testingController.getButton(BACK).whenPressed(new InstantCommand(() -> {
//            regression.add(localizationManager.getLidar().getDistance(), hood.getPosition());
//            SmartDashboard.putNumberArray("regression/Xarr", regression.getX());
//            SmartDashboard.putNumberArray("regression/Yarr", regression.getY());
//        }));
//
//        testingController.getButton(START).whenPressed(new InstantCommand(() -> {
//            regression.run();
//            System.out.println(Arrays.toString(regression.get()));
//        }));
//
//        testingController.getButton(X).toggleWhenPressed(new ShooterManualSetpoint(shooter,
//                testingController.getAxis(ChickenXboxController.XboxAxis.LEFT_X)));
//
//        testingController.getButton(DPadAxis.UP).toggleWhenPressed(new HoodManualControl(hood,
//                testingController.getAxis(ChickenXboxController.XboxAxis.RIGHT_X)));
//
//        testingController.getButton(DPadAxis.DOWN).toggleWhenPressed(new PointToTarget(localizationManager.getNavX(), driveTrain, driverController, localizationManager.getLimelight()));

    }

    private void initDefaultCommands() {
        logger.info("Initializing default commands...");

        driveTrain.setDefaultCommand(new PointDrive(driveTrain, localizationManager.getNavX(),
                driverController.getAxis2D(ChickenXboxController.Hand.RIGHT),
                driverController.getAxis(ChickenXboxController.XboxAxis.LEFT_X),
                driverController.getButton(ChickenXboxController.XboxButton.Y)));

//        driveTrain.setDefaultCommand(new PointToTarget(localizationManager.getNavX(), driveTrain, driverController, localizationManager.getLimelight()));

//        intake.setDefaultCommand(new InstCommand(() -> intake.setPercent(0), intake).perpetually());
//        indexer.setDefaultCommand(new IndexerBallQueueSequence(indexer, funnel));

        shooter.setDefaultCommand(new InstCommand(() -> shooter.setPercent(0), shooter).perpetually());
//        hood.setDefaultCommand(new InstCommand(() -> hood.setPercent(0), hood).perpetually());

        hood.setDefaultCommand(new HoodManualControl(hood, copilotController.getAxis(ChickenXboxController.XboxAxis.LEFT_X)));
        climber.setDefaultCommand(new InstCommand(() -> climber.setPercent(0), climber).perpetually());

//        controlPanel.setDefaultCommand();
    }

    private void initModeTransitionBindings() {
        logger.info("Initializing mode transition bindings...");

        var inTeleop = new Trigger(RobotState::isOperatorControl);
        var inAuto = new Trigger(RobotState::isAutonomous);
        var inTest = new Trigger(RobotState::isTest);
        var enabled = new Trigger(RobotState::isEnabled);
        var disabled = new Trigger(RobotState::isDisabled);

//        inAuto.whenActive(() -> {
//            ;
//        });

        enabled.whenActive(() -> {
            // enable brakes
            driveTrain.setBrakes(NeutralMode.Brake);
            indexer.setBrake(NeutralMode.Brake);
            climber.setBrake(NeutralMode.Brake);
            logger.info("Mechanism brakes enabled");
            shooter.disableMotors();
        });

        disabled.whenActive(new WaitCommand(2)
                .alongWith(new InstCommand(() -> logger.debug("Disabling mechanism brakes in 2 seconds"), true))
                .andThen(new ConditionalCommand(new InstCommand(true), new InstCommand(() -> {
                    // disable brakes
                    driveTrain.setBrakes(NeutralMode.Coast);
                    indexer.setBrake(NeutralMode.Coast);
                    climber.setBrake(NeutralMode.Coast);
                    logger.info("Mechanism brakes disabled");
                }, true), RobotState::isEnabled)));
    }

    Command getAutoCommand() {
        // TODO logic for selecting an auto command
        return new InstantCommand(() -> {
            climber.zero();
        });
//        return new FollowRamsetePath(driveTrain, List.of(new Pose2d(0, 0, new Rotation2d(0)), new Pose2d(3, 0, new Rotation2d(0))), false);
    }

    private void initDashboard() {
        SmartDashboard.putData(new InstCommand(() -> climber.zero(), true));
    }
//
//    if (TEST_MODE){
//        driveTrain.setDefaultCommand(new TankDrive(driveTrain, driverController));
//        funnel.setDefaultCommand(new FunnelManualControl(funnel,
//                copilotController.getAxis2D(ChickenXboxController.Hand.LEFT).withDeadzone(0.1)));
//        indexer.setDefaultCommand(new IndexerManualControl(indexer,
//                copilotController.getAxis(ChickenXboxController.XboxAxis.LEFT_X).withDeadzone(0.1)));
//        funnel.setDefaultCommand(new FunnelManualControl(funnel,
//                copilotController.getAxis2D(ChickenXboxController.Hand.LEFT).withDeadzone(0.1)));
//        intake.setDefaultCommand(new IntakeManualControl(intake,
//                copilotController.getAxis(ChickenXboxController.XboxAxis.LEFT_X).withDeadzone(0.1)));
//        climber.setDefaultCommand(new ClimberManualControl(climber,
//                testClimbController.getAxis(ChickenXboxController.XboxAxis.LEFT_X),
//                testClimbController.getButton(org.checkerframework.checker.units.qual.A)));
//    }
//
}
