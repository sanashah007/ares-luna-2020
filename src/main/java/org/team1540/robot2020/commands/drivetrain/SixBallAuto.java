package org.team1540.robot2020.commands.drivetrain;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import org.team1540.robot2020.LocalizationManager;
import org.team1540.robot2020.commands.climber.Climber;
import org.team1540.robot2020.commands.funnel.Funnel;
import org.team1540.robot2020.commands.hood.Hood;
import org.team1540.robot2020.commands.hood.HoodZeroSequence;
import org.team1540.robot2020.commands.indexer.Indexer;
import org.team1540.robot2020.commands.indexer.IndexerBallQueueSequence;
import org.team1540.robot2020.commands.intake.Intake;
import org.team1540.robot2020.commands.intake.IntakePercent;
import org.team1540.robot2020.commands.shooter.Shooter;
import org.team1540.robot2020.utils.ChickenXboxController;
import org.team1540.robot2020.utils.InstCommand;

import java.util.List;

public class SixBallAuto extends ParallelCommandGroup {
    private LocalizationManager localizationManager;
    private Pose2d startingPose;

    public SixBallAuto(DriveTrain driveTrain, Intake intake, Funnel funnel, Indexer indexer, Shooter shooter, Hood hood, Climber climber, LocalizationManager localizationManager, ChickenXboxController driverController) {
        this.localizationManager = localizationManager;
        ShooterLineUpSequence shooterLineUpSequence = new ShooterLineUpSequence(driveTrain, indexer, shooter, hood, driverController, localizationManager, true);
        ShooterLineUpSequence shooterLineUpSequence2 = new ShooterLineUpSequence(driveTrain, indexer, shooter, hood, driverController, localizationManager, true);
        addCommands(
                new InstCommand(() -> {
                    climber.zero();
                    climber.setRatchet(Climber.RatchetState.DISENGAGED);
                }),
                sequence(
                        new HoodZeroSequence(hood),
                        race(
                                shooterLineUpSequence,
                                sequence(
                                        new ShootOneBall(intake, funnel, indexer, localizationManager, shooterLineUpSequence::isLinedUp),
                                        new ShootOneBall(intake, funnel, indexer, localizationManager, shooterLineUpSequence::isLinedUp),
                                        new ShootOneBall(intake, funnel, indexer, localizationManager, shooterLineUpSequence::isLinedUp)
                                )
                        ),
                        race(
                                new IntakePercent(intake, 1),
                                sequence(
                                        new IndexerBallQueueSequence(indexer, funnel, false),
                                        new IndexerBallQueueSequence(indexer, funnel, false),
                                        new IndexerBallQueueSequence(indexer, funnel, false),
                                        new IndexerBallQueueSequence(indexer, funnel, false),
                                        new IndexerBallQueueSequence(indexer, funnel, false)
                                ),
                                sequence(
                                        new PointToAngle(driveTrain, localizationManager, this::getStartingPose, new Pose2d(0, 0, new Rotation2d(Math.toRadians(-150))), Math.toRadians(20)),
                                        new ChickenRamseteCommand(
                                                this::getStartingPose,
                                                () -> List.of(
                                                        new Pose2d(0, 0, new Rotation2d(Math.toRadians(-150))),
                                                        new Pose2d(-2, -.8, new Rotation2d(Math.PI)),
                                                        new Pose2d(-4, -.8, new Rotation2d(Math.PI))
                                                ),
                                                false, localizationManager, driveTrain
                                        )
                                )
                        ),
                        race(
                                shooterLineUpSequence2,
                                sequence(
                                        new ShootOneBall(intake, funnel, indexer, localizationManager, shooterLineUpSequence2::isLinedUp),
                                        new ShootOneBall(intake, funnel, indexer, localizationManager, shooterLineUpSequence2::isLinedUp),
                                        new ShootOneBall(intake, funnel, indexer, localizationManager, shooterLineUpSequence2::isLinedUp)
                                )
                        )
                )
        );
    }

    @Override
    public void initialize() {
        Pose2d startingPose = localizationManager.odometryGetPose();
        this.startingPose = new Pose2d(startingPose.getTranslation(), new Rotation2d());
        super.initialize();
    }

    private Pose2d getStartingPose() {
        return startingPose;
    }
}