package io.extact.msa.spring.rms.console.screen;

import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;

public interface RmsScreen {

    Transition play(boolean printHeader);
}