package com.jonli.fundkeeper;
import java.lang.reflect.Field;
import android.app.Application;
import android.graphics.Typeface;
/*設置字體*/
public class SetTypeface extends Application{
    public static Typeface typeFace;

    @Override
    public void onCreate() {
        super.onCreate();
        setTypeface();
    }
    public void setTypeface(){

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
        try
        {
            Field field_3 = Typeface.class.getDeclaredField("SANS_SERIF");
            field_3.setAccessible(true);
            field_3.set(null, typeFace);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}