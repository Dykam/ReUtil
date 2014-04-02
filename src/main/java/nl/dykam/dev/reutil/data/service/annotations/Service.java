package nl.dykam.dev.reutil.data.service.annotations;

import nl.dykam.dev.reutil.data.Component;
import nl.dykam.dev.reutil.data.service.ComponentService;
import nl.dykam.dev.reutil.ticker.TickerService;

public @interface Service {
    Class<? extends ComponentService> value();
}
