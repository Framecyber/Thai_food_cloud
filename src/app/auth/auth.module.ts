import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- สำคัญสำหรับ [(ngModel)]

import { AuthRoutingModule } from './auth-routing.module';
import { WelcomeComponent } from './welcome/welcome.component';
import { SignupComponent } from './signup/signup.component';
import { LoginComponent } from './login/login.component';

// --- นำเข้า PrimeNG Modules ---
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';


@NgModule({
  declarations: [
    WelcomeComponent,
    SignupComponent,
    LoginComponent
  ],
  imports: [
    CommonModule,
    AuthRoutingModule,
    FormsModule, // <-- เพิ่ม FormsModule ที่นี่
    // --- เพิ่ม PrimeNG Modules ที่นี่ ---
    CardModule,
    InputTextModule,
    ButtonModule,
    PasswordModule
  ]
})
export class AuthModule { }