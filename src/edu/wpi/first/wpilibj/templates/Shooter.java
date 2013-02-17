/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Eric
 */
public class Shooter
{
    public Shooter(Piston piston, SpeedController motor, float spin_up_time)
    {
        Initialize(piston, motor, spin_up_time);
    }
    
    public void Fire()
    {
        ChangeState(SHOOTER_FIRE);
    }

    public void TurnOff()
    {
        ChangeState(SHOOTER_OFF);
    }

    public void TurnOn()
    {
        ChangeState(SHOOTER_ON);
    }
    
    public void Update()
    {
        switch (m_current_state)
        {
        case SHOOTER_UNKNOWN:
            // Get to the Off state
            m_piston.SetState(Piston.PISTON_OUT);
            m_speed_controller.set(0);
            if (!m_piston.IsMoving())
            {
                SetState(SHOOTER_OFF);
            }
            break;
        case SHOOTER_OFF:
            m_piston.SetState(Piston.PISTON_OUT);
            m_speed_controller.set(0);

            if (m_next_state != SHOOTER_OFF)
            {
                m_speed_controller.set(1);
                SetState(SHOOTER_TURNING_ON);
            }
            break;
        case SHOOTER_TURNING_ON:
            // Wait for the motor to spin up
            m_speed_controller.set(SHOOTER_MOTOR_SPEED);
            m_piston.SetState(Piston.PISTON_OUT);
            if (m_transition_timer.get() >= m_spin_up_time)
            {
                SetState(SHOOTER_ON);
            }
            break;
        case SHOOTER_ON:
            m_speed_controller.set(SHOOTER_MOTOR_SPEED);
            m_piston.SetState(Piston.PISTON_OUT);
            switch (m_next_state)
            {
                case SHOOTER_OFF:
                    SetState(SHOOTER_OFF);
                    break;
                case SHOOTER_TURNING_ON:
                    // Shouldn't ever happen, but if so, we are on, so correct it
                    m_next_state = SHOOTER_ON;
                    break;
                case SHOOTER_ON:
                    // Nothing to do
                    break;
                case SHOOTER_FIRE:
                    SetState(SHOOTER_FIRE);
                    break;
                case SHOOTER_RESETTING:
                    // Again, an error, but handle as if we should file
                    SetState(SHOOTER_FIRE);
                    break;
                }
                break;
        case SHOOTER_FIRE:
            m_piston.SetState(Piston.PISTON_IN);
            m_speed_controller.set(1.0f);

            if (!m_piston.IsMoving())
            {
                SetState(SHOOTER_RESETTING);
            }
            break;
        case SHOOTER_RESETTING:
            m_piston.SetState(Piston.PISTON_OUT);
            m_speed_controller.set(SHOOTER_MOTOR_SPEED);
            if (!m_piston.IsMoving())
            {
                SetState(SHOOTER_ON);
            }
            break;
        }
    }

    private void ChangeState(int next_state)
    {
        // Ignore unkown, we never want to go to unkown
        if (next_state == SHOOTER_UNKNOWN)
        {
            return;
        }

        m_next_state = next_state;
    }

    private void SetState(int current_state)
    {
        m_current_state = current_state;
        m_transition_timer.start();
        m_transition_timer.reset();
    }
    
    private void Initialize(Piston piston, SpeedController motor, float spin_up_time)
    {
        m_speed_controller = motor;
        m_piston = piston;
        m_spin_up_time = spin_up_time;
        ChangeState(SHOOTER_OFF);
    }
    
    private static final int SHOOTER_UNKNOWN = 0;
    private static final int SHOOTER_OFF = 1;           // Piston out, motor off,   			possible next state
    private static final int SHOOTER_TURNING_ON = 2;	// Piston out, motor just turned on
    private static final int SHOOTER_ON = 3;		// Piston out, motor at full speed		possible next state
    private static final int SHOOTER_FIRE = 4;		// Piston moving in, motor at full speed	possible next state
    private static final int SHOOTER_RESETTING = 5;	// Piston moving out, motor at full 
    private static final float SHOOTER_MOTOR_SPEED = 1;

    private int                 m_current_state = SHOOTER_UNKNOWN;
    private int                 m_next_state = SHOOTER_UNKNOWN;
    private SpeedController	m_speed_controller;
    private Piston		m_piston;
    private Timer               m_transition_timer;
    private float               m_spin_up_time = 1;
};
		
	
		
		

 

