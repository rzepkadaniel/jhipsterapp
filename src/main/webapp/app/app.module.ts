import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { JhipsterappSharedModule } from 'app/shared/shared.module';
import { JhipsterappCoreModule } from 'app/core/core.module';
import { JhipsterappAppRoutingModule } from './app-routing.module';
import { JhipsterappHomeModule } from './home/home.module';
import { JhipsterappEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    JhipsterappSharedModule,
    JhipsterappCoreModule,
    JhipsterappHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    JhipsterappEntityModule,
    JhipsterappAppRoutingModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [MainComponent]
})
export class JhipsterappAppModule {}
