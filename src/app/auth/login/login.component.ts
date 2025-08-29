// src/app/auth/login/login.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  // เพิ่มแค่ state สำหรับแสดงโมดัล Welcome
  showWelcome = false;

  constructor(private router: Router) { }

  onLogin() {
    // ตรวจสอบ Login (ตามเดิม)
    if (this.username === 'AWS0102@gmail.com' && this.password === 'Bank007') {
      // แทนที่ alert เดิมด้วยการเปิดโมดัล Welcome
      this.showWelcome = true;
      // ไม่ navigate ทันที เพื่อให้ผู้ใช้กด OK ก่อน (พฤติกรรมเหมือน alert)
    } else {
      // ส่วนนี้ไม่แตะ (ยังใช้ alert แจ้ง error ตามเดิม)
      alert('Invalid username or password.');
    }
  }

  // ปิดโมดัล แล้วค่อยไปหน้า /menu (แทนการกด OK บน alert)
  closeWelcome() {
    this.showWelcome = false;
    this.router.navigate(['/menu']);
  }
}
