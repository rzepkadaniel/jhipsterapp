import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'person',
        loadChildren: () => import('./person/person.module').then(m => m.JhipsterappPersonModule)
      },
      {
        path: 'glasses',
        loadChildren: () => import('./glasses/glasses.module').then(m => m.JhipsterappGlassesModule)
      },
      {
        path: 'biker',
        loadChildren: () => import('./biker/biker.module').then(m => m.JhipsterappBikerModule)
      },
      {
        path: 'bike',
        loadChildren: () => import('./bike/bike.module').then(m => m.JhipsterappBikeModule)
      },
      {
        path: 'driver',
        loadChildren: () => import('./driver/driver.module').then(m => m.JhipsterappDriverModule)
      },
      {
        path: 'car',
        loadChildren: () => import('./car/car.module').then(m => m.JhipsterappCarModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class JhipsterappEntityModule {}
