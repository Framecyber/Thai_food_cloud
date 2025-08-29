import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  email: string = '';
  password: string = '';
  confirmPassword: string = '';

  // state สำหรับโชว์โมดัล Welcome (เพิ่มเฉพาะส่วนนี้)
  showWelcome = false;

  constructor(private router: Router) {}

  onSignUp() {
    // เงื่อนไขเดิม: เช็ครหัสไม่ตรงยังคงใช้ alert ได้ตามเดิม
    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match!');
      return;
    }

    // เปลี่ยนเฉพาะ "สมัครสำเร็จ" จาก alert -> โมดัล Welcome
    this.showWelcome = true;
    // ไม่ navigate ทันที เพื่อให้ผู้ใช้กด OK ก่อน (พฤติกรรมเหมือน alert)
  }

  // กด OK บนโมดัลแล้วค่อยไปหน้า /menu (ตามโค้ดเดิมของคุณ)
  closeWelcome() {
    this.showWelcome = false;
    this.router.navigate(['/menu']);
  }
}
