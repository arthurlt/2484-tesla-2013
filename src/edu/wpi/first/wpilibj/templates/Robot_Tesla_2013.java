/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
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
    private static final int SLOT = 1; //Only a single slot
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
    private static final int TRIGGERS = 3; //right is positive, left is negative
    private static final int RIGHT_X = 4;
    private static final int RIGHT_Y = 5;
    private static final int A_BUT = 1;
    private static final int B_BUT = 2;
    private static final int X_BUT = 3;
    private static final int Y_BUT = 4;
    private static final int LEFT_BUMP = 5;
    private static final int RIGHT_BUMP = 6;
    private static final int BACK_BUT = 7;
    private static final int START_BUT = 8;
    private static final int LEFT_BUT = 9;
    private static final int RIGHT_BUT = 10;
    
    Joystick m_Driver; //Driver controller
    Joystick m_Secondary; //Shooter and Secondary controller
    
    //Drive
    RobotDrive m_RobotDrive;
    
    //Pneumatics
    Compressor m_Compressor;
        
    private static final int SOL_ARM = 2;
    private static final int SOL_FRISBEE = 1;
    
    Solenoid m_ArmPist;
    Solenoid m_FrisbeePist;

    //Limit Switches
    private static final int ARMTOP = 1;
    private static final int ARMBOT = 2;
    
    DigitalInput m_ArmTop;
    DigitalInput m_ArmBot;

    //Driver Station
    DriverStationLCD m_LCD;
    
    //Static Variables
    int BButTog = 0;
    int SetSpin = 0;
    boolean m_BButtonWasDown = false;
    int XButTog = 0;
    boolean m_XButtonWasDown = false;
    
    protected void robotInit() 
    {
        m_LeftDriveMotor = new Victor(SLOT, LEFT_MOTOR_CHANNEL); //cRIO Slot,Channel
        m_RightDriveMotor = new Victor(SLOT, RIGHT_MOTOR_CHANNEL);
        m_ArmMotor = new Victor(SLOT, ARM_MOTOR);
        m_FrisbeeMotor = new Victor(SLOT, FRISBEE_MOTOR);
        m_Motor_5 = new Victor(SLOT, MOTOR_CHANNEL_5);
        
        m_Driver = new Joystick(1); //USB Port
        m_Secondary = new Joystick(2); 
        m_RobotDrive = new RobotDrive(m_LeftDriveMotor, m_RightDriveMotor);
        
        m_Compressor = new Compressor(14, 1); //Pressure switch channel,Relay channel
        m_Compressor.start(); 
        
        m_ArmPist = new Solenoid(SOL_ARM);
        m_FrisbeePist = new Solenoid(SOL_FRISBEE);
        
        m_ArmTop = new DigitalInput(ARMTOP); //Channel
        m_ArmBot = new DigitalInput(ARMBOT);
    }  
    
    /**
     * This function is called for the drive system.
     */
    public void drive()
    {
        /*
         *  Left analog stick moves left side of robot's drive
         *  Right analog stick moves right side of robot's drive
         */
        m_RobotDrive.tankDrive(motorFix(m_Driver.getRawAxis(LEFT_Y))*-1, motorFix(m_Driver.getRawAxis(RIGHT_Y))*-1, false);
        //m_Driver.getRawAxis()*-1 to invert
        
    }
    
    /**
     * Fixes input axis that isn't centered
     * 
     * @param axis Input your RawAxis
     * @return Returns fixed axis
     */
    public double motorFix(double axis)
    {
        if (axis <= .05 && axis >= -.05) 
        {
            return 0;
        }
        else
        {
            return axis;
        }
    }
    
    public void arm()
    {
        /*
         *  X button fires arm's piston
         *  Select+X retracts arm's piston
         *  Left analog stick moves arm's hooks up and down
         */
        double armDir = motorFix(m_Secondary.getRawAxis(LEFT_Y)); //Reading secondary left Y axis
        if (armDir < 0) //If going down..
        { 
            if (m_ArmBot.get()) //..and it hits the bottom..
            {
                armDir = 0; //..stop
            }
        }
        else
        { //Or, if going up..
            if (m_ArmTop.get()) //..and it hits the top..
            {
                armDir = 0; //..stop
            }
        }
        m_ArmMotor.set(armDir);
        
//        m_XButtonDown = getRawButton(X_BUT);
//        m_XbuttonPressed = m_XButtonDown && !m_XButtonWasDown;
//        m_XButtonWasDown = m_XButtonDown;
//        
//        m_BackButtonDown = getRawButton(BACK_BUT);
//        m_BackButtonPressed = m_BackButtonDown && !m_BackButtonWasDown;
//        m_BackButtonWasDown = m_BackButtonDown;
//        
//        
//             
//        if (m_Secondary.getRawButton(X_BUT))
//        {
//            if (XButTog == 0)
//            {
//                m_ArmPist.set(true);
//            }
//            if (m_Secondary.getRawButton(BACK_BUT))
//            {
//                m_ArmPist.set(false);
//            }
//        }
        //X for extend toggle, left analog stick for up and down
    }
    
    public void frisbee()
    {
        /*
         *  B button toggles frisbee firing motor
         *  Right trigger fires frisbee firing piston
         */
        double TrigDown = m_Secondary.getRawAxis(TRIGGERS); //Reading secondary trigger axis
        if (m_Secondary.getRawButton(B_BUT)) //Checking secondary B button
        {
            if (!m_BButtonWasDown)
            {
                if (BButTog == 1)
                {
                    BButTog = 0; //Toggling B button
                    SetSpin = 0; //Stopping spin
                }
                else
                {
                    BButTog = 1; //Toggling B button
                    SetSpin = 1; //Spinning full power
                }
                m_FrisbeeMotor.set(SetSpin*-1); //Seting spin
            }
            m_BButtonWasDown = true; //B button WAS down
        }
        else
        {
            m_BButtonWasDown = false; //B button WASN'T down
        }
        if (TrigDown < 0) //If right trigger is down
        {
            m_FrisbeePist.set(true); //Fire frisbee piston
        }
        else
        {
            m_FrisbeePist.set(false); //Retract frisbee piston
        }
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
            frisbee(); //Call frisbee thrower function
            arm(); //Call arm function
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
    
    public void disabled()
    {
        
    }
   
}
