package de.egym.recruiting.codingtask.sort;

import com.google.inject.AbstractModule;

public class SortModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SortingService.class).to(SortingServiceImpl.class);
    }
    
}
