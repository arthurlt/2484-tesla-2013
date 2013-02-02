/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


//import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot_Tesla_2013 extends SimpleRobot 
{
    //Motors
    private static final int cRIO_SLOT = 1; //Only a single slot
    private static final int LEFT_MOTOR_CHANNEL = 1;
    private static final int RIGHT_MOTOR_CHANNEL = 2;
    private static final int ARM_MOTOR = 3;
    private static final int FRISBEE_MOTOR = 4;
    private static final int MOTOR_CHANNEL_5 = 5; //Rename
    
    SpeedController m_LeftDriveMotor;
    SpeedController m_RightDriveMotor;
    SpeedController m_ArmMotor;
    SpeedController m_FrisbeeMotor;
    SpeedController m_Motor_5; //Rename
    
    //Xbox Controllers
    private static final int LEFT_X = 1;
    private static final int LEFT_Y = 2;
    private static final int UNKNOWN = 3; //triggers?
    private static final int RIGHT_X = 4;
    private static final int RIGHT_Y = 5;
    
    Joystick m_Driver; //Driver controller
    Joystick m_Secondary; //Shooter and Secondary controller
    
    //Drive
    RobotDrive m_RobotDrive;
    
    //Compressor
    Compressor m_Compressor;
    
    //Limit Switches
    private static final int ARMTOP = 1;
    private static final int ARMBOT = 2;
    
    DigitalInput m_ArmTop;
    DigitalInput m_ArmBot;

    
    protected void robotInit() 
    {
        m_LeftDriveMotor = new Victor(cRIO_SLOT, LEFT_MOTOR_CHANNEL); //cRIO Slot,Channel
        m_RightDriveMotor = new Victor(cRIO_SLOT, RIGHT_MOTOR_CHANNEL);
        m_ArmMotor = new Victor(cRIO_SLOT, ARM_MOTOR);
        m_FrisbeeMotor = new Victor(cRIO_SLOT, FRISBEE_MOTOR);
        m_Motor_5 = new Victor(cRIO_SLOT, MOTOR_CHANNEL_5);
        
        m_Driver = new Joystick(1); //USB Port
        m_Secondary = new Joystick(2); 
        m_RobotDrive = new RobotDrive(m_LeftDriveMotor, m_RightDriveMotor);
        
        m_Compressor = new Compressor(cRIO_SLOT,1); //cRIO Slot,Channel
        m_Compressor.start(); 
        
        m_ArmTop = new DigitalInput(ARMTOP); //Channel
        m_ArmBot = new DigitalInput(ARMBOT);
    }  
    
    /**
     * This function is called for the drive system.
     */
    public void drive()
    {
        m_RobotDrive.tankDrive(motorFixL()*-1, motorFixR()*-1, false);
        //m_Driver.getRawAxis()*-1 to invert
        
    }
    
    public double motorFixL()
    {
        if (m_Driver.getRawAxis(LEFT_Y) <= .1 && m_Driver.getRawAxis(LEFT_Y) >= -.1) {
            return 0;
        } else {
            return m_Driver.getRawAxis(LEFT_Y);
        }
    }
    
    public double motorFixR()
    {
        if (m_Driver.getRawAxis(RIGHT_Y) <= .1 && m_Driver.getRawAxis(RIGHT_Y) <= -.1) {
            return 0;
        } else {
            return m_Driver.getRawAxis(RIGHT_Y);
        }
    }
    
    public void arm()
    {
        //X for extend toggle, left analog stick for up and down
        //ArmTop.get(); //Use this to get a Boolean value
        //ArmBot.get
        /*if (isMoving) {
        } else {
        } */
    }
    
    public void frisbee()
    {
        //B for spinup toggle, right trigger for piston fire
    }
    
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() 
    {
        getWatchdog().setEnabled(false);
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() 
    {
        getWatchdog().setEnabled(true);
        while (isOperatorControl() && isEnabled()) // loop during enabled teleop mode
            {             
            drive(); //Call drive function
            //frisbee(); 
            //arm();
            getWatchdog().feed(); //Feed the dog
            Timer.delay(0.005); //Delay loop
            }     
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() 
    {
    
    }
   
}
