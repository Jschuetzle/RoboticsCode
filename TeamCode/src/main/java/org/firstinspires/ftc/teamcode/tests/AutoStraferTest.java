package org.firstinspires.ftc.teamcode.tests;



import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;

@Autonomous(name = "AutoStraferTest", group = "Test")
public class AutoStraferTest extends LinearOpMode {

    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private DcMotor rightArm;
    private DcMotor leftArm;

    private Servo rightHand;
    private Servo leftHand;

    private NormalizedColorSensor colorSensor;

    static final double TICKS_PER_MOTOR_REV = 537.7;
    static final double WHEEL_DIAMETER_INCHES = 3.93701;
    static final double TICKS_PER_INCH = TICKS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * 3.141592);

    //Movement Methods

    @Override
    public void runOpMode() throws InterruptedException{
      //Motor Hardware Map:
        //Defines motors and direction
        configureMotors(hardwareMap, "motor1", "motor2", "motor3", "motor4", "motor5", "motor6", "rightHand", "leftHand");



        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "color_sensor");

        //SET DIRECTIONS
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection((DcMotor.Direction.FORWARD));
        backRight.setDirection(DcMotor.Direction.FORWARD);

        rightArm.setDirection(DcMotor.Direction.FORWARD);
        leftArm.setDirection(DcMotor.Direction.FORWARD);

        rightHand.setDirection(Servo.Direction.FORWARD);
        leftHand.setDirection(Servo.Direction.REVERSE);

        //ENCODER SET MODES
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //SET ZERO POWER BEHAVIOR
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Set Motors to Use No Power
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);

        rightHand.setPosition(0);
        leftHand.setPosition(0);

        //<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

        telemetry.addData("status", "Initialized");
        telemetry.update();

        //<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
        //Motor Run to Position

        /* Encoder for Movement
        int leftTargetPosition;
        int rightTargetPosition;
        int leftInches = 280;
        int rightInches = 280;

        leftTargetPosition = frontLeft.getCurrentPosition() + (int)(leftInches * TICKS_PER_INCH);
        rightTargetPosition = frontRight.getCurrentPosition() + (int)(rightInches * TICKS_PER_INCH);
        frontLeft.setTargetPosition(leftTargetPosition);
        frontRight.setTargetPosition(rightTargetPosition);
        */



        waitForStart();

        while (opModeIsActive()){



            colorSensor.setGain(5);
            final float[] hsvValues = new float[3];

            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            Color.colorToHSV(colors.toColor(), hsvValues);

            //Outputs Color Data
            telemetry.addData("Path0",  "Starting at %7d ",
                    frontRight.getCurrentPosition());
            telemetry.addLine()
                    .addData("Red", "%.3f", colors.red)
                    .addData("Green", "%.3f", colors.green)
                    .addData("Blue", "%.3f", colors.blue);
            telemetry.addLine()
                    .addData("Hue", "%.3f", hsvValues[0])
                    .addData("Saturation", "%.3f", hsvValues[1])
                    .addData("Value", "%.3f", hsvValues[2]);
            telemetry.addData("Alpha", "%.3f", colors.alpha);
            telemetry.update();


            //telemetry.addData("Target Position: ", leftTargetPosition);
            //telemetry.addData("Target Position: ", rightTargetPosition);

            //frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            if ((colors.red/colors.blue) > 1.5 && (colors.red/colors.green) > 1.5){
                //Stops Motors
                stopMoving();
            } else {
                //Move Forward
                moveForward(0.6);
            }

        }
    }

    public void strafeLeft(double power) {
        frontLeft.setPower(-power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(-power);
    }

    public void strafeRight(double power){
        frontLeft.setPower(power);
        frontRight.setPower(-power);
        backLeft.setPower(-power);
        backRight.setPower(power);
    }

    public void moveForward(double power){
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    public void moveBackward(double power){
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(-power);
        backRight.setPower(-power);
    }

    public void stopMoving(){
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
    }

    public void configureMotors(HardwareMap hw, String rfName, String lfName, String rbName, String lbName, String raName, String laName, String rhName, String lhName){
        frontRight = hw.dcMotor.get(rfName);
        frontLeft = hw.dcMotor.get(lfName);
        backRight = hw.dcMotor.get(rbName);
        backLeft = hw.dcMotor.get(lbName);
        rightArm = hw.dcMotor.get(raName);
        leftArm = hw.dcMotor.get(laName);
        rightHand = hw.servo.get(rhName);
        leftHand = hw.servo.get(lhName);
    }


}
