/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Eric
 */

public final class Piston
{
    public static final boolean PISTON_IN = false;
    public static final boolean PISTON_OUT = true;
    
    public Piston(Solenoid sol, boolean initially_out, boolean invert, float transition_time)
    {
        Initialize(sol, null, initially_out, invert, transition_time);
    }

    public Piston(Solenoid in_sol, Solenoid out_sol, boolean initially_out, boolean invert, float transition_time)
    {
        Initialize(in_sol, out_sol, initially_out, invert, transition_time);
    }

    public void SetState(boolean set_out)
    {
        //Don't do anything if not changing states
        if (GetState() == set_out)
        {
            return;
        }

        m_solenoid.set(set_out ^ m_invert);
        if (m_out_solenoid != null)
        {
            m_out_solenoid.set(!(set_out ^ m_invert));
        }
        m_transition_timer.start();
        m_transition_timer.reset();
    }

    public boolean GetState()
    {
        return m_solenoid.get() ^ m_invert;
    }
	
    public boolean IsMoving()
    {
        return m_transition_timer.get() < m_transition_time;
    }
    
    private void Initialize(Solenoid in_sol, Solenoid out_sol, boolean initially_out, boolean invert, float transition_time)
    {
        m_solenoid      = in_sol;
        m_out_solenoid  = out_sol;
        m_invert        = invert;
        m_transition_time   = transition_time;
        m_transition_timer  = new Timer();
        SetState(initially_out);
    }

    private Solenoid    m_solenoid;
    private Solenoid    m_out_solenoid;
    private float       m_transition_time;
    private boolean     m_invert;
    private Timer       m_transition_timer;
};