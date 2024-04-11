import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {
  HorseCreateEditComponent,
  HorseCreateEditMode
} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {TournamentCreateComponent} from "./component/tournament/tournament-create/tournament-create.component";
import {TournamentStandingsComponent} from "./component/tournament/tournament-standings/tournament-standings.component";
import {HorseInfoComponent} from "./component/horse/horse-info/horse-info.component";
import {TournamentComponent} from "./component/tournament/tournament.component";

const routes: Routes = [
  {path: '', redirectTo: 'horses', pathMatch: 'full'},
  {path: 'horses', children: [
    {path: '', component: HorseComponent},
    {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.create}},
    {path: ':id/edit', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.edit}},
    {path: ':id/info', component: HorseInfoComponent}
  ]},
  {path: 'tournaments', children: [
    {path: '', component: TournamentComponent},
    {path: 'create', component: TournamentCreateComponent},
    {path: ':id/standings', component: TournamentStandingsComponent}
  ]},
  {path: '**', redirectTo: 'horses'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
