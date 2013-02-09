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
import edu.wpi.first.wpilibj.DriverStationLCD.Line;
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
    int SLOT = 1; //Only a single slot
    int LEFT_MOTOR_CHANNEL = 1;
    int RIGHT_MOTOR_CHANNEL = 2;
    int ARM_MOTOR = 3;
    int FRISBEE_MOTOR = 4;
    int MOTOR_CHANNEL_5 = 5; //Rename
    
    SpeedController m_LeftDriveMotor;
    SpeedController m_RightDriveMotor;
    SpeedController m_ArmMotor;
    SpeedController m_FrisbeeMotor;
    SpeedController m_Motor_5; //Rename
    
    //Xbox Controllers
    int LEFT_X = 1;
    int LEFT_Y = 2;
    int TRIGGERS = 3; //right is positive, left is negative
    int RIGHT_X = 4;
    int RIGHT_Y = 5;
    int A_BUT = 1;
    int B_BUT = 2;
    int X_BUT = 3;
    int Y_BUT = 4;
    int LEFT_BUMP = 5;
    int RIGHT_BUMP = 6;
    int BACK_BUT = 7;
    int START_BUT = 8;
    int LEFT_BUT = 9;
    int RIGHT_BUT = 10;
    
    Joystick m_Driver; //Driver controller
    Joystick m_Secondary; //Shooter and Secondary controller
    
    //Drive
    RobotDrive m_RobotDrive;
    
    //Pneumatics
    Compressor m_Compressor;
        
    int SOL_ARM = 2;
    int SOL_FRISBEE = 1;
    
    Solenoid m_ArmPist;
    Solenoid m_FrisbeePist;

    //Limit Switches
    int ARMTOP = 1;
    int ARMBOT = 2;
    
    DigitalInput m_ArmTop;
    DigitalInput m_ArmBot;

    //Driver Station
    DriverStationLCD m_LCD; 
    
    //Static Variables
    boolean m_TrigRightWasDown = false;
    boolean m_ArmPistonIn = false;
    boolean m_XButWasDown = false;
    boolean m_ArmPistonBringIn = false;
    boolean m_BackButWasDown = false;
    boolean m_XBackButWasDown = false;
    
    boolean m_FrisbeeMotorSpin = false;
    int SetSpin = 0;
    boolean m_BButWasDown = false;
    int ShotsFired = 0;
    
    int LCDCol = 1;
    
    //Buttons
    boolean m_XButPressed;
    boolean m_BackButPressed;
    boolean m_XBackButPressed;
    boolean m_BButPressed;
    boolean m_TrigRightPressed;
    
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
        
        m_LCD = DriverStationLCD.getInstance();
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
    
    public void readButtons()
    {
        boolean XButDown = m_Secondary.getRawButton(X_BUT);
        m_XButPressed = XButDown && !m_XButWasDown;
        m_XButWasDown = XButDown;
        
        boolean BackButDown = m_Secondary.getRawButton(BACK_BUT);
        m_BackButPressed = BackButDown && !m_BackButWasDown;
        m_BackButWasDown = BackButDown;
        
        boolean XBackButDown = XButDown && BackButDown;
        m_XBackButPressed = XBackButDown && !m_XBackButWasDown;
        m_XBackButWasDown = XBackButDown;
        
        boolean BButDown = m_Secondary.getRawButton(B_BUT);
        m_BButPressed = BButDown && !m_BButWasDown;
        m_BButWasDown = BButDown;
        
        double TrigRight = m_Secondary.getRawAxis(TRIGGERS);
        boolean TrigRightDown = false;
        if (TrigRight < -.06)
        {
            TrigRightDown = true;
        }
        else if (TrigRight < -.03)
        {
            TrigRightDown = false;
        }
        m_TrigRightPressed = TrigRightDown && !m_TrigRightWasDown;
        m_TrigRightWasDown = TrigRightDown;
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
                m_LCD.println(Line.kUser2, LCDCol, "Hooks hit bottom ");
            }
        }
        else
        { //Or, if going up..
            if (m_ArmTop.get()) //..and it hits the top..
            {
                armDir = 0; //..stop
                m_LCD.println(Line.kUser2, LCDCol, "Hooks hit top    ");
            }
        }
        m_ArmMotor.set(armDir);
        
        if (m_XButPressed)
        {
            if (m_ArmPistonIn)
            {
                m_ArmPist.set(true); //Firing arm's piston 
                m_ArmPistonIn = false; //
                m_LCD.println(Line.kUser1, LCDCol, "Arm is out");
            }
            else
            {
                m_ArmPistonIn = true; //
                m_LCD.println(Line.kUser1, LCDCol, "Arm is in ");
            }
        }

        if (m_BackButPressed) //Checking secondary Back button
        {
            if (m_ArmPistonBringIn)
            {
                m_ArmPistonBringIn = false; //
            }
            else
            {
                m_ArmPistonBringIn = true; //
            }
        }
        
        if (m_XBackButPressed)
        {
            m_ArmPist.set(false); //Retracting arm's piston
        }
        //X for extend toggle, left analog stick for up and down
    }
    
    public void frisbee()
    {
        /*
         *  B button toggles frisbee firing motor
         *  Right trigger fires frisbee firing piston
         */
        
        if (m_BButPressed)
        {
            if (m_FrisbeeMotorSpin)
            {
                m_FrisbeeMotorSpin = false; //
                SetSpin = 0; //Stopping spin
                m_LCD.println(Line.kUser4, LCDCol, "Press B to spin-up");
            }
            else
            {
                m_FrisbeeMotorSpin = true; //Toggling B button
                SetSpin = 1; //Spinning full power
                m_LCD.println(Line.kUser4, LCDCol, "Ready to fire!    ");
            }
        m_FrisbeeMotor.set(SetSpin*-1); //Seting spin
        }
        
        if (m_TrigRightPressed)
        {
            m_FrisbeePist.set(true);
            ShotsFired++;
            m_LCD.println(Line.kUser5, LCDCol, ShotsFired + " shots fired");
        }
        else
        {
            m_FrisbeePist.set(false);
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
        getWatchdog().setExpiration(2);
        m_LCD.println(Line.kUser1, LCDCol, "Arm is in ");
        m_LCD.println(Line.kUser2, LCDCol, "Hooks are neutral");
        m_LCD.println(Line.kUser2, LCDCol, "Hooks hit bottom");
        m_LCD.println(Line.kUser4, LCDCol, "Press B to spin-up");
        m_LCD.println(Line.kUser5, LCDCol, "0 shots fired");
        while (isOperatorControl() && isEnabled()) // loop during enabled teleop mode
            {     
            readButtons();
            drive(); //Call drive function
            frisbee(); //Call frisbee thrower function
            arm(); //Call arm function
            m_LCD.updateLCD(); //Updating the LCD
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
