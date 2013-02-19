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
    private static final int SLOT = 1; //Only a single slot
    private static final int LEFT_MOTOR_CHANNEL     = 1;
    private static final int RIGHT_MOTOR_CHANNEL    = 2;
    private static final int ARM_MOTOR              = 3;
    private static final int FRISBEE_MOTOR          = 4;
    
    SpeedController m_LeftDriveMotor;
    SpeedController m_RightDriveMotor;
    SpeedController m_ArmMotor;
    SpeedController m_FrisbeeMotor;
    
    //Xbox Controllers
    private static final int LEFT_X     = 1;
    private static final int LEFT_Y     = 2;
    private static final int TRIGGERS   = 3; //right is positive, left is negative
    private static final int RIGHT_X    = 4;
    private static final int RIGHT_Y    = 5;
    private static final int A_BUT      = 1;
    private static final int B_BUT      = 2;
    private static final int X_BUT      = 3;
    private static final int Y_BUT      = 4;
    private static final int LEFT_BUMP  = 5;
    private static final int RIGHT_BUMP = 6;
    private static final int BACK_BUT   = 7;
    private static final int START_BUT  = 8;
    private static final int LEFT_BUT   = 9;
    private static final int RIGHT_BUT  = 10;
    
    Joystick m_Driver; //Driver controller
    Joystick m_Secondary; //Shooter and Secondary controller
    
    //Drive
    RobotDrive m_RobotDrive;
    
    //Pneumatics
    Compressor m_Compressor;
    private static final int SOL_ARM_IN     = 3;
    private static final int SOL_ARM_OUT    = 4;
    private static final int SOL_FRISBEE_IN = 2;
    private static final int SOL_FRISBEE_OUT = 1;
    private static final int SOL_LIGHTS     = 5;
    
    Solenoid m_ArmSolIn;
    Solenoid m_ArmSolOut;
    Solenoid m_FrisbeeSolIn;
    Solenoid m_FrisbeeSolOut;
    Solenoid m_Lights;
    
    Piston m_ArmPist;
    Piston m_FrisbeePist;
    
    Shooter m_Shooter;

    //Limit Switches
    private static final int ARMTOP = 2;
    private static final int ARMBOT = 1;
    
    DigitalInput m_ArmTop;
    DigitalInput m_ArmBot;

    //Driver Station
    DriverStationLCD m_LCD; 
    
    //Variables
    boolean m_TrigRightWasDown  = false;
    boolean m_ArmPistonOut      = false;
    boolean m_XButWasDown       = false;
    boolean m_ArmPistonBringIn  = false;
    boolean m_BackButWasDown    = false;
    boolean m_XBackButWasDown   = false;
    
    boolean m_YButWasDown       = false;
    
    boolean m_FrisbeeMotorSpin  = false;
    int m_SetSpin               = 0;
    boolean m_BButWasDown       = false;
    boolean m_FrisbeeFired      = false;
    int m_ShotsFired            = 0;
    
    int LCDCol                  = 1;
    
    boolean TrigRightDown       = false;
    double LeftDriveFinal;
    double RightDriveFinal;
    
    //Buttons
    boolean m_XButPressed;
    boolean m_BackButPressed;
    boolean m_XBackButPressed;
    boolean m_YButPressed;
    boolean m_BButPressed;
    boolean m_TrigRightPressed;
    
    //Strings
    String ArmDirBegin  = "Hooks are set. ";
    String ArmBotString = "Reached bottom.";
    String ArmTopString = "Reached top.   ";
    
    String ArmPosBegin  = "Arm is in. ";
    String ArmOutString = "Arm is out.";
    String ArmInString  = "Arm is in. ";
    
    String FrisbeeBegin = "Press B to spin-up.";
    String SpunUpString = "Ready to fire!     ";
    String NoSpinString = "Press B to spin-up.";
    
    String ShotsBegin   = "0 shots fired.  ";
    String ShotsString  =  " shots fired.";
    
    protected void robotInit() 
    {
        m_LeftDriveMotor    = new Victor(SLOT, LEFT_MOTOR_CHANNEL); //cRIO Slot,Channel
        m_RightDriveMotor   = new Victor(SLOT, RIGHT_MOTOR_CHANNEL);
        m_ArmMotor          = new Victor(SLOT, ARM_MOTOR);
        m_FrisbeeMotor      = new Victor(SLOT, FRISBEE_MOTOR);
        
        m_Driver        = new Joystick(1); //USB Port
        m_Secondary     = new Joystick(2); 
        m_RobotDrive    = new RobotDrive(m_LeftDriveMotor, m_RightDriveMotor);
        
        m_Compressor = new Compressor(14, 1); //Pressure switch channel,Relay channel
        m_Compressor.start(); 
        
        m_ArmSolIn      = new Solenoid(SOL_ARM_IN);
        m_ArmSolOut     = new Solenoid(SOL_ARM_OUT);
        m_FrisbeeSolIn  = new Solenoid(SOL_FRISBEE_IN);
        m_FrisbeeSolOut = new Solenoid(SOL_FRISBEE_OUT);
        m_Lights        = new Solenoid(SOL_LIGHTS);
        
        m_ArmPist       = new Piston(m_ArmSolIn, m_ArmSolOut, false, true, 3);
        m_FrisbeePist   = new Piston(m_FrisbeeSolIn, m_FrisbeeSolOut, true, false, 0.5f);
        
        m_Shooter = new Shooter(m_FrisbeePist, m_FrisbeeMotor, 1);
        
        m_ArmTop = new DigitalInput(ARMTOP); //Channel
        m_ArmBot = new DigitalInput(ARMBOT);
        
        m_LCD = DriverStationLCD.getInstance();
        
        
    }  
    /**
     *  Call this to reset the robot.
     */
    public void reset()
    {
        //Pistons
        if (m_ArmPistonOut) //If Arm is out..
        {
            m_ArmSolIn.set(true); //pull it back in
            m_ArmSolOut.set(false);
        }
        
        //Variables
        m_TrigRightWasDown  = false;
        m_ArmPistonOut      = false;
        m_XButWasDown       = false;
        m_ArmPistonBringIn  = false;
        m_BackButWasDown    = false;
        m_XBackButWasDown   = false;
        
        m_YButWasDown       = false;

        m_FrisbeeMotorSpin  = false;
        m_SetSpin           = 0;
        m_BButWasDown       = false;
        m_FrisbeeFired      = false;
        m_ShotsFired        = 0;
        
        //Strings
        m_LCD.println(Line.kUser1, LCDCol, ArmPosBegin);
        m_LCD.println(Line.kUser2, LCDCol, ArmDirBegin);
        m_LCD.println(Line.kUser4, LCDCol, FrisbeeBegin);
        m_LCD.println(Line.kUser5, LCDCol, ShotsBegin);
        m_LCD.println(Line.kUser6, LCDCol, "                   ");
        
        m_LCD.updateLCD();
    }
    
    /**
     *  Call this to use read the controllers' inputs.
     */
    public void readInputs()
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
        
        boolean YButDown = m_Secondary.getRawButton(Y_BUT);
        m_YButPressed = YButDown && !m_YButWasDown;
        m_YButWasDown = YButDown;
        
        boolean BButDown = m_Secondary.getRawButton(B_BUT);
        m_BButPressed = BButDown && !m_BButWasDown;
        m_BButWasDown = BButDown;
        
        double TrigRight = m_Secondary.getRawAxis(TRIGGERS);
        if (TrigRight < -.2)
        {
            TrigRightDown = true;
        }
        else if (TrigRight > -.1)
        {
            TrigRightDown = false;
        }
        m_TrigRightPressed = TrigRightDown && !m_TrigRightWasDown;
        m_TrigRightWasDown = TrigRightDown;
        
        int LeftPos;
        int RightPos;
        double LeftDrive = motorFix(m_Driver.getRawAxis(LEFT_Y));
        double RightDrive = motorFix(m_Driver.getRawAxis(RIGHT_Y));
        if (LeftDrive > 0)
        {
            LeftPos = 1;
        }
        else
        {
            LeftPos = -1;
        }
        if (RightDrive > 0)
        {
            RightPos = 1;
        }
        else
        {
            RightPos = -1;
        }
        LeftDriveFinal = (LeftDrive*LeftDrive)*LeftPos;
        RightDriveFinal = (RightDrive*RightDrive)*RightPos;
    }
    
    /**
     * Fixes input axis that isn't centered.
     * 
     * @param axis Input your RawAxis
     * @return Returns fixed axis
     */
    public double motorFix(double axis)
    {
        double DeadZone = .05;
        double range = 1.0 - DeadZone;
        if (axis <= DeadZone && axis >= -DeadZone) 
        {
            return 0;
        }
        else if (axis < -DeadZone)
        {
            return (axis + DeadZone)/range;
        }
        else 
        {
            return (axis - DeadZone)/range;
        }
    }
    
    /**
     * Call this function to use the drive system.
     */
    public void drive()
    {
        m_RobotDrive.tankDrive(LeftDriveFinal*-1, RightDriveFinal*-1, true);
        m_LCD.println(Line.kUser3, LCDCol, "Left: " + LeftDriveFinal + "  Right: " + RightDriveFinal);
        //m_Driver.getRawAxis()*-1 to invert
        //"Left: "LeftDriveFinal + " Right: "RightDriveFinal
    }
    
    /**
     *  Call this to use the climbing arm.
     */
    public void arm()
    {
        double armDir = motorFix(m_Secondary.getRawAxis(LEFT_Y)); //Reading secondary left Y axis
        
        if (armDir > 0) //If going down..
        { 
            if (m_ArmBot.get()) //..and it hits the bottom..
            {
                armDir = 0; //..stop
                m_LCD.println(Line.kUser2, LCDCol, ArmBotString);
            }
        }
        else
        { //Or, if going up..
            if (m_ArmTop.get()) //..and it hits the top..
            {
                armDir = 0; //..stop
                m_LCD.println(Line.kUser2, LCDCol, ArmTopString);
            }
        }
        m_ArmMotor.set(armDir);
        
        if (m_XButPressed)
        {
            if (!m_ArmPist.GetState())
            {
                m_ArmPist.SetState(Piston.PISTON_OUT);
                m_LCD.println(Line.kUser1, LCDCol, ArmOutString);
            }
//            if (!m_ArmPistonOut)
//            {
//                m_ArmSolIn.set(false);
//                m_ArmSolOut.set(true); //Moving arm out
//                m_ArmPistonOut = true;
//                m_LCD.println(Line.kUser1, LCDCol, ArmOutString);
//            }
        }

        if (m_BackButPressed) //Checking secondary Back button
        {
            if (m_ArmPistonBringIn)
            {
                m_ArmPistonBringIn = false;
            }
            else
            {
                m_ArmPistonBringIn = true;
            }
        }
        
        if (m_XBackButPressed)
        {
            if (m_ArmPist.GetState())
            {
                m_ArmPist.SetState(Piston.PISTON_IN);
                m_LCD.println(Line.kUser1, LCDCol, ArmInString);
            }
//            if (m_ArmPistonOut)
//            {
//              m_ArmSolIn.set(true);
//              m_ArmSolOut.set(false); //Retracting arm
//              m_ArmPistonOut = false; 
//              m_LCD.println(Line.kUser1, LCDCol, ArmInString);
//            }
        }
    }
    
    /**
     *  Call this to use the Frisbee shooter.
     */
    public void frisbee()
    {     
        if (m_BButPressed)
        {
            if (m_FrisbeeMotorSpin)
            {
                m_FrisbeeMotorSpin = false;
                m_Shooter.TurnOff();
                //m_SetSpin = 0; //Stopping spin
            }
            else
            {
                m_FrisbeeMotorSpin = true;
                m_Shooter.TurnOn();
                //m_SetSpin = 1; //Spinning full power
            }
        //m_FrisbeeMotor.set(m_SetSpin*-1); //Seting spin
        }
        
        if (m_Shooter.GetState() < Shooter.SHOOTER_ON)
        {
            m_LCD.println(Line.kUser4, LCDCol, NoSpinString);
        }
        else 
        {
            m_LCD.println(Line.kUser4, LCDCol, SpunUpString);
        }
        
        if (m_TrigRightPressed)
        {
            m_Shooter.Fire();
            m_ShotsFired++;
            m_LCD.println(Line.kUser5, LCDCol, m_ShotsFired + ShotsString);
//            if (!m_FrisbeeFired)
//            {
//                m_FrisbeeFired = true;
//                m_FrisbeeSolIn.set(true);
//                m_FrisbeeSolOut.set(false); //Pushing frisbee into firing motor
//                m_ShotsFired++;
//                m_LCD.println(Line.kUser5, LCDCol, m_ShotsFired + ShotsString);
//            }
//            else
//            {
//                m_FrisbeeFired = false;
//                m_FrisbeeSolIn.set(false);
//                m_FrisbeeSolOut.set(true); //Grabbing another frisbee
//            }
        }
    }
    
    /**
     *  Call this to test the Limit Switches.
     */
    public void limitSwitch()
    {   
        if (m_ArmBot.get())
        {
            m_LCD.println(Line.kUser6, LCDCol, "Bottom Limit Switch");
        }
        else if (m_ArmTop.get())
        {
            m_LCD.println(Line.kUser6, LCDCol, "Top Limit Switch   ");
        }
        else
        {
            m_LCD.println(Line.kUser6, LCDCol, "                   ");
        }
    }
    
    public void lights()
    {
        if (m_YButPressed)
        {
            if (m_Lights.get())
            {
                m_Lights.set(false);
            }
            else
            {
                m_Lights.set(true);
            }
        }
    }
    
    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() 
    {
        getWatchdog().setEnabled(true);
        //getWatchdog().setExpiration(2); //Use this to change Watchdog timout, time in seconds
        reset();
        while (isOperatorControl() && isEnabled()) // loop during enabled teleop mode
            {     
            readInputs();
            drive(); //Call drive function
            frisbee(); //Call frisbee thrower function
            arm(); //Call arm function
            limitSwitch();
            lights();
            m_Shooter.Update(); //Updated the shooter state, [BROKEN] causes crash
            m_LCD.updateLCD(); //Updating the LCD
            getWatchdog().feed(); //Feed the dog
            Timer.delay(0.005); //Delay loop
            }     
    }
        
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() 
    {
        double LeftAuto     = -.5;
        double RightAuto    = -.5;
        int DriveTimer      = 4;
        int AutoShotsFired  = 0;

        getWatchdog().setEnabled(false);
        m_RobotDrive.tankDrive(LeftAuto, RightAuto, false);
        Timer.delay(DriveTimer);
        m_RobotDrive.tankDrive(0, 0, false);
        m_Shooter.TurnOn();
        while (AutoShotsFired <= 3)
        {
            m_Shooter.Fire();
            AutoShotsFired++;
        }
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() 
    {
    
    }
    
    /**
     * This function is called once each time the robot is disabled.
     */
    public void disabled()
    {
        reset();
    }
   
}
