package nl.dykam.dev.reutil.data.service;

import nl.dykam.dev.reutil.data.Component;
import nl.dykam.dev.reutil.data.annotations.Defaults;
import nl.dykam.dev.reutil.data.annotations.Instantiation;

@Defaults(instantiation = Instantiation.Manual)
public class NoComponent extends Component<Object> {
}
