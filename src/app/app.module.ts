import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
// **ไม่ต้อง import WelcomeComponent, LoginComponent, SignupComponent, MenuComponent ที่นี่แล้ว**

@NgModule({
  declarations: [
    AppComponent
    // **เอา WelcomeComponent, LoginComponent, SignupComponent, MenuComponent ออก**
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule 
    // **เอา FormsModule และ PrimeNG Modules ออก เพราะจะไปอยู่ในโมดูลย่อยแทน**
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
