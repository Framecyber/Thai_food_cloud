import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Import Routing ของส่วนนี้
import { MenuRoutingModule } from './menu-routing.module';

// Import PrimeNG Modules ที่ใช้ในหน้าเมนู
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DataViewModule } from 'primeng/dataview';

// Import Component ของส่วนนี้
import { MenuComponent } from './menu.component';

@NgModule({
  declarations: [
    MenuComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    MenuRoutingModule,
    // เพิ่ม PrimeNG Modules
    ButtonModule,
    InputTextModule,
    DataViewModule
  ]
})
export class MenuModule { }
