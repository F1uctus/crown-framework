package com.crown.time;

import com.crown.creatures.Creature;
import com.crown.i18n.I18n;
import com.crown.i18n.ITemplate;
import org.jetbrains.annotations.NonNls;

import java.lang.reflect.Method;

public abstract class Action<T extends Creature> {
    private T performer;

    public Action(T performer) {
        this.performer = performer;
    }

    public abstract ITemplate perform();

    public abstract ITemplate rollback();

    public T getPerformer() {
        return performer;
    }

    public void setPerformer(T value) {
        performer = value;
    }

    public static Action<Creature> change(Creature c, @NonNls String changerMethodName, int delta) {
        Method m;
        try {
            m = Creature.class.getDeclaredMethod(changerMethodName, int.class);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

        return new Action<>(c) {
            @Override
            public ITemplate perform() {
                try {
                    return (ITemplate) m.invoke(getPerformer(), delta);
                } catch (Throwable e) {
                    return I18n.of(e.getMessage());
                }
            }

            @Override
            public ITemplate rollback() {
                try {
                    return (ITemplate) m.invoke(getPerformer(), -delta);
                } catch (Throwable e) {
                    return I18n.of(e.getMessage());
                }
            }
        };
    }
}
