import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { ActivateComponent } from './components/activate/activate.component';
import { UploadComponent } from './components/upload/upload.component';
import { VideoDetailComponent } from './components/video-detail/video-detail.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'activate', component: ActivateComponent },
  { path: 'home', component: HomeComponent },
  { path: 'video/:id', component: VideoDetailComponent },
  { path: 'profile/:id', component: ProfileComponent },
  { path: 'upload', component: UploadComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }