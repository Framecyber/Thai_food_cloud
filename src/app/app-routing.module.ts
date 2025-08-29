// src/app/app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'auth', pathMatch: 'full' },
  { path: 'auth', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  // *** เพิ่มบรรทัดนี้สำหรับ MenuModule ***
  { path: 'menu', loadChildren: () => import('./pages/menu/menu.module').then(m => m.MenuModule) },
  // สามารถเพิ่มเส้นทางอื่นๆ หรือ wild card route ได้ที่นี่
  // { path: '**', redirectTo: 'auth' } // ตัวอย่าง: ถ้าไม่มี path ที่ตรง ให้กลับไปหน้า auth
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }