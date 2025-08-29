import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MenuService, MenuItem, Category } from './menu.service';

interface ReceiptLine { name: string; qty: number; unitPrice: number; lineTotal: number; }
interface BillOptions { discountRate?: number; discountFixed?: number; serviceChargeRate?: number; vatRate?: number; }
interface Receipt { lines: ReceiptLine[]; subtotal: number; discount: number; serviceCharge: number; vat: number; grandTotal: number; }

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  selectedCategory: 'all' | Category = 'all';
  searchTerm = '';

  allMenuItems: MenuItem[] = [];
  filteredMenuItems: MenuItem[] = [];

  order: { item: MenuItem; qty: number }[] = [];

  discountPct = 0; discountFixed = 0; serviceChargePct = 10; vatPct = 7;
  billOptions: BillOptions = { discountRate: 0, discountFixed: 0, serviceChargeRate: 0.10, vatRate: 0.07 };
  receipt: Receipt | null = null;

  showCart = false; @ViewChild('cartDialog') cartDialog?: ElementRef<HTMLDivElement>;
  @ViewChild('cartCloseBtn') cartCloseBtn?: ElementRef<HTMLButtonElement>;

  showOptions = true; showTotals = true;

  // ถ้า image ว่าง/โหลดไม่ได้ จะใช้รูปนี้แทน
  fallbackImg = 'https://placehold.co/1200x800?text=Food+Image';

  // >>> เพิ่มเฉพาะสำหรับเงื่อนไขข้อ 1–2
  showThanks = false;
  orderId = '';

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.fetchMenus();
    this.syncRates();
    this.recalc();
  }

  // ===== ดึงข้อมูลจาก backend =====
  fetchMenus(): void {
    this.menuService.getMenus().subscribe({
      next: items => {
        this.allMenuItems = items.map(m => ({ ...m, image: m.image || this.fallbackImg }));
        this.applyFilters();
      },
      error: err => {
        console.error('โหลดเมนูล้มเหลว', err);
        this.allMenuItems = [];
        this.applyFilters();
      }
    });
  }

  // ===== กรอง/ค้นหา =====
  filterMenu(category: 'all' | Category){ this.selectedCategory = category; this.applyFilters(); }
  applyFilters(){
    let items = this.allMenuItems;
    if (this.selectedCategory !== 'all') items = items.filter(i => i.category === this.selectedCategory);
    if (this.searchTerm){
      const q = this.searchTerm.trim().toLowerCase();
      items = items.filter(i => i.name.toLowerCase().includes(q) || i.description.toLowerCase().includes(q));
    }
    this.filteredMenuItems = items;
  }

  // ===== ตะกร้า/คิดเงิน =====
  get distinctItemsCount(){ return this.order.length; }

  addToOrder(item: MenuItem){
    const line = this.order.find(l => l.item.id === item.id);
    if (line) line.qty += 1; else this.order.push({ item, qty: 1 });
    this.recalc();
  }
  increaseQty(line:{item:MenuItem; qty:number}){ line.qty += 1; this.recalc(); }
  decreaseQty(line:{item:MenuItem; qty:number}){ line.qty -= 1; if (line.qty<=0) this.order = this.order.filter(l=>l!==line); this.recalc(); }
  removeLine(line:{item:MenuItem; qty:number}){ this.order = this.order.filter(l=>l!==line); this.recalc(); }
  clearOrder(){ this.order = []; this.recalc(); }

  openCart(){ this.showCart = true; setTimeout(() => this.cartCloseBtn?.nativeElement.focus(), 0); }
  closeCart(){ this.showCart = false; }
  toggleOptions(){ this.showOptions = !this.showOptions; }
  toggleTotals(){ this.showTotals = !this.showTotals; }

  private toSatang(v:number){ return Math.round((v||0)*100); }
  private fromSatang(s:number){ return s/100; }

  syncRates(){
    this.billOptions.discountRate = Math.max(0,(this.discountPct||0)/100);
    this.billOptions.discountFixed = Math.max(0,this.discountFixed||0);
    this.billOptions.serviceChargeRate = Math.max(0,(this.serviceChargePct||0)/100);
    this.billOptions.vatRate = Math.max(0,(this.vatPct||0)/100);
    this.recalc();
  }

  private computeReceipt(opts: BillOptions): Receipt{
    const subtotalSat = this.order.reduce((sum,l)=> sum + this.toSatang(l.item.price)*l.qty, 0);
    let discountSat = 0;
    if (opts.discountFixed) discountSat += this.toSatang(opts.discountFixed);
    if (opts.discountRate)  discountSat += Math.round(subtotalSat * opts.discountRate);
    if (discountSat > subtotalSat) discountSat = subtotalSat;

    const baseAfterDiscount = subtotalSat - discountSat;
    const serviceSat = opts.serviceChargeRate ? Math.round(baseAfterDiscount * opts.serviceChargeRate) : 0;
    const vatSat = opts.vatRate ? Math.round((baseAfterDiscount + serviceSat) * opts.vatRate) : 0;

    return {
      lines: this.order.map(l => ({ name:l.item.name, qty:l.qty, unitPrice:l.item.price, lineTotal:l.item.price*l.qty })),
      subtotal: this.fromSatang(subtotalSat),
      discount: this.fromSatang(discountSat),
      serviceCharge: this.fromSatang(serviceSat),
      vat: this.fromSatang(vatSat),
      grandTotal: this.fromSatang(baseAfterDiscount + serviceSat + vatSat),
    };
  }

  recalc(){ this.receipt = this.computeReceipt(this.billOptions); }

  printReceipt(){ window.print(); }

  // >>> ปรับตามเงื่อนไข: กดแล้วขึ้นโมดัล “ขอบคุณที่อุดหนุน” + โชว์เลขออเดอร์
  confirmOrder(){
    this.orderId = this.genOrderId();   // ข้อ 2: สร้างเลขออเดอร์ เช่น D123
    this.showThanks = true;             // ข้อ 1: แสดงโมดัลแทน alert ที่มีหัว “localhost:xxxx says”
    this.closeCart();
  }

  onImgError(ev: Event){ (ev.target as HTMLImageElement).src = this.fallbackImg; }

  // ===== helper: สร้างเลขออเดอร์รูปแบบ D123 (3 หลัก) =====
  private genOrderId(): string {
    const n = Math.floor(100 + Math.random() * 900); // 100–999
    return 'D' + n;
  }
}
