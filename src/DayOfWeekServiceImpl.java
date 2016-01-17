import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WaiTuck on 11/01/2016.
 */
public class DayOfWeekServiceImpl implements DayOfWeekService, ApplicationComponent {


    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    public String getDayOfWeek() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        return String.format("Hello World! It's %s", simpleDateFormat.format(new Date()));
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "DayFinder";
    }
}
