package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous
public class Gyro extends LinearOpMode {
    Robot robot = new Robot();

    private ElapsedTime runtime = new ElapsedTime();

    private Orientation lastAngles = new Orientation();
    private double currAngle = 0.0;


    @Override
    public void runOpMode() throws InterruptedException{
        robot.init(hardwareMap, "auto");

        waitForStart();


    }



    public void resetAngle(){
        lastAngles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        currAngle = 0;
    }

    public double getAngle(){
        Orientation orientation = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = orientation.firstAngle - lastAngles.firstAngle;

        if(deltaAngle > 180){
            deltaAngle -= 360;
        } else if(deltaAngle < -180){
            deltaAngle += 360;
        }

        currAngle += deltaAngle;
        lastAngles = orientation;
        return currAngle;
    }

    public void turn(double degrees){
        resetAngle();

        double error = degrees;

        while(opModeIsActive() && Math.abs(error) > 2){
            double motorPower = (error < 0 ? -0.3 : 0.3);
            robot.setWheelPower(-motorPower, motorPower, -motorPower, motorPower);
            error = degrees - getAngle();
        }

        robot.setAllWheelPower(0);
    }

    public void turnTo(double degrees){
        Orientation orientation = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double error = degrees - orientation.firstAngle;

        if(error > 180){
            error -= 360;
        } else if(error < 180){
            error += 360;
        }

        turn(error);
    }

    public double getAbsoluteAngle(){
        return robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }


    public void turnToPID(double targetAngle){
        TurnPIDController pid = new TurnPIDController(targetAngle, 0.01, 0, 0.003);
        while(opModeIsActive() && Math.abs(targetAngle - getAbsoluteAngle()) > 1){
            double motorPower = pid.update(getAbsoluteAngle());
            robot.setWheelPower(motorPower, -motorPower, motorPower, -motorPower);
        }
        robot.setAllWheelPower(0);
    }

    public void turnPID(double degrees){
        turnToPID(degrees + getAbsoluteAngle());
    }
}