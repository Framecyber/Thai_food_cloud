// menu.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

const API_BASE = 'https://servicebackporpear-env.eba-bvnhpcbc.us-east-1.elasticbeanstalk.com/api';
const DEFAULT_IMAGE = 'https://placehold.co/1200x800?text=Food+Image';

export type Category = 'appetizer' | 'main-course' | 'dessert' | 'drink';

export interface BackendMenu {
  id?: number;               // บางแบ็กเอนด์ใช้ id
  id_item?: number;          // บางแบ็กเอนด์ใช้ id_item
  name?: string;             // บางแบ็กเอนด์ใช้ name
  item_name?: string;        // บางแบ็กเอนด์ใช้ item_name
  description?: string | null;
  price?: number | string | null;
  image_url?: string | null;
  category_id?: number | null;
  is_available?: boolean | 0 | 1;
}

export interface MenuItem {
  id: number;
  name: string;
  description: string;
  price: number;
  image: string;
  category: Category;
  isAvailable: boolean;
}

/** 1) บังคับหมวดจากรายชื่อที่คุณกำหนด */
const FORCED_BY_NAME: Record<string, Category> = {
  // ของทานเล่น
  'หมูสะเต๊ะ': 'appetizer',
  'ไก่ทอดสมุนไพร': 'appetizer',

  // อาหารจานหลัก
  'ผัดไทยกุ้งสด': 'main-course',
  'ต้มยำกุ้ง': 'main-course',
  'ไข่เจียวปู': 'main-course',

  // ของหวาน
  'ข้าวเหนียวมะม่วง': 'dessert',
  'ลอดช่อง': 'dessert',

  // เครื่องดื่ม (รองรับหลายสะกด)
  'ชาไทย': 'drink',
  'แป๊ปซี่': 'drink',
  'เป๊ปซี่': 'drink',
  'เป็บซี่': 'drink',
};

/** 2) fallback จาก category_id ของ DB (ถ้าอยากให้ยังมีผล) */
const CATEGORY_BY_ID: Record<number, Category> = {
  1: 'appetizer',
  2: 'main-course',
  3: 'dessert',
  4: 'drink',
  5: 'drink', // กันกรณี DB ใส่ 5 เป็นเครื่องดื่ม
};

/** mapping ย้อนกลับเพื่อส่งเข้าแบ็กเอนด์ */
const CATEGORY_TO_ID: Record<Category, number> = {
  'appetizer': 1,
  'main-course': 2,
  'dessert': 3,
  'drink': 4, // ถ้าแบ็กเอนด์ใช้ 5 ให้แก้เป็น 5
};

/** 3) คีย์เวิร์ด เผื่อชื่อไม่ตรงเป๊ะ */
type Rule = { keywords: string[]; category: Category };
const RULES: Rule[] = [
  { keywords: ['หมูสะเต๊ะ'], category: 'appetizer' },
  { keywords: ['ไก่ทอด'], category: 'appetizer' },

  { keywords: ['ผัดไทย'], category: 'main-course' },
  { keywords: ['ต้มยำ'], category: 'main-course' },
  { keywords: ['ไข่เจียว'], category: 'main-course' },

  { keywords: ['ข้าวเหนียว','มะม่วง'], category: 'dessert' },
  { keywords: ['ลอดช่อง'], category: 'dessert' },

  { keywords: ['ชาไทย'], category: 'drink' },
  { keywords: ['pepsi'], category: 'drink' },
];

/** helpers */
function cleanUrl(u?: string | null): string {
  let x = (u ?? '').trim();
  if (!x) return '';
  if ((x.startsWith("'") && x.endsWith("'")) || (x.startsWith('"') && x.endsWith('"'))) {
    x = x.slice(1, -1).trim();
  }
  return x;
}

function resolveCategory(nameRaw: string, catId?: number | null): Category {
  const name = (nameRaw ?? '').trim();

  // A) บังคับตามรายชื่อ (ชนะทุกกรณี)
  if (FORCED_BY_NAME[name]) return FORCED_BY_NAME[name];

  // B) ถ้าไม่อยู่ในรายชื่อ → ลองใช้ category_id จาก DB
  if (typeof catId === 'number' && CATEGORY_BY_ID[catId]) return CATEGORY_BY_ID[catId];

  // C) คีย์เวิร์ด
  const norm = name.toLowerCase();
  for (const r of RULES) {
    if (r.keywords.every(k => norm.includes(k.toLowerCase()))) return r.category;
  }

  // D) ไม่เข้าอะไรเลย ก็ให้เป็น appetizer ไปก่อน
  return 'appetizer';
}

@Injectable({ providedIn: 'root' })
export class MenuService {
  private http = inject(HttpClient);

  /** แปลงรูปแบบจากแบ็กเอนด์ → UI */
  private readonly fromBackend = (r: BackendMenu): MenuItem => {
    const id  = (r.id_item ?? r.id) as number;
    const name = (r.item_name ?? r.name ?? '').trim();
    const image = cleanUrl(r.image_url) || DEFAULT_IMAGE;
    const category = resolveCategory(name, r.category_id);
    const price = Number(r.price ?? 0);
    const isAvailable = typeof r.is_available === 'boolean'
      ? r.is_available
      : !!Number(r.is_available ?? 1);

    return {
      id,
      name,
      description: r.description ?? '',
      price,
      image,
      category,
      isAvailable,
    };
  };

  /** แปลงรูปแบบจาก UI → payload ที่แบ็กเอนด์เข้าใจ */
  private readonly toBackendPayload = (ui: Partial<MenuItem>): BackendMenu => {
    return {
      id_item: ui.id,
      item_name: ui.name,
      description: ui.description ?? null,
      price: ui.price ?? 0,
      image_url: ui.image ?? null,
      category_id: ui.category ? CATEGORY_TO_ID[ui.category] : null,
      is_available: typeof ui.isAvailable === 'boolean' ? (ui.isAvailable ? 1 : 0) : 1
    };
  };

  /** GET รายการทั้งหมด */
  getMenus(): Observable<MenuItem[]> {
    return this.http.get<BackendMenu[]>(`${API_BASE}/menus`).pipe(
      map(rows => rows.map(this.fromBackend))
    );
  }

  /** GET รายการเดียวตาม id */
  getMenu(id: number): Observable<MenuItem> {
    return this.http.get<BackendMenu>(`${API_BASE}/menus/${id}`).pipe(
      map(this.fromBackend)
    );
  }

  /** POST (JSON) สร้างเมนูใหม่ – ใช้เมื่อรูปเป็น URL/ไม่มีการอัปโหลดไฟล์ */
  createMenu(input: Omit<MenuItem, 'id'>): Observable<MenuItem> {
    const payload = this.toBackendPayload(input);
    return this.http.post<BackendMenu>(`${API_BASE}/menus`, payload).pipe(
      map(this.fromBackend)
    );
  }

  /**
   * POST (multipart/form-data) สร้างเมนูใหม่ + อัปโหลดไฟล์รูป
   * (อย่าตั้ง Content-Type เอง ให้เบราว์เซอร์ใส่ boundary อัตโนมัติ)
   */
  createMenuWithFile(
    input: Omit<MenuItem, 'id' | 'image'>,
    file: File
  ): Observable<MenuItem> {
    const fd = new FormData();
    fd.append('item_name', input.name);
    fd.append('description', input.description ?? '');
    fd.append('price', String(input.price ?? 0));
    fd.append('category_id', String(CATEGORY_TO_ID[input.category]));
    fd.append('is_available', input.isAvailable ? '1' : '0');
    fd.append('image', file); // ให้ฟิลด์นี้ตรงกับที่แบ็กเอนด์รับ

    return this.http.post<BackendMenu>(`${API_BASE}/menus`, fd).pipe(
      map(this.fromBackend)
    );
  }

  /** PATCH (JSON) อัปเดตข้อมูลเมนูบางส่วน */
  updateMenu(
    id: number,
    input: Partial<Omit<MenuItem, 'id'>>
  ): Observable<MenuItem> {
    // ส่งเฉพาะฟิลด์ที่มีค่า เพื่อเลี่ยงทับ null โดยไม่ตั้งใจ
    const partialPayload: Partial<BackendMenu> = {};
    if (input.name !== undefined) partialPayload.item_name = input.name;
    if (input.description !== undefined) partialPayload.description = input.description ?? null;
    if (input.price !== undefined) partialPayload.price = input.price ?? 0;
    if (input.image !== undefined) partialPayload.image_url = input.image ?? null;
    if (input.category !== undefined) partialPayload.category_id = CATEGORY_TO_ID[input.category];
    if (input.isAvailable !== undefined) partialPayload.is_available = input.isAvailable ? 1 : 0;

    return this.http.patch<BackendMenu>(`${API_BASE}/menus/${id}`, partialPayload).pipe(
      map(this.fromBackend)
    );
  }

  /**
   * PATCH (multipart/form-data) อัปเดตพร้อมอัปโหลดไฟล์ใหม่
   * ใช้เมื่อแบ็กเอนด์รองรับอัปเดตรูปด้วย multipart
   */
  updateMenuWithFile(
    id: number,
    input: Partial<Omit<MenuItem, 'id' | 'image'>>,
    file?: File
  ): Observable<MenuItem> {
    const fd = new FormData();
    if (input.name !== undefined) fd.append('item_name', input.name);
    if (input.description !== undefined) fd.append('description', input.description ?? '');
    if (input.price !== undefined) fd.append('price', String(input.price ?? 0));
    if (input.category !== undefined) fd.append('category_id', String(CATEGORY_TO_ID[input.category]));
    if (input.isAvailable !== undefined) fd.append('is_available', input.isAvailable ? '1' : '0');
    if (file) fd.append('image', file);

    return this.http.patch<BackendMenu>(`${API_BASE}/menus/${id}`, fd).pipe(
      map(this.fromBackend)
    );
  }

  /** DELETE ลบเมนู */
  deleteMenu(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/menus/${id}`);
  }

  /** สะดวก ๆ: toggle สถานะเปิดขาย */
  toggleAvailability(id: number, on: boolean): Observable<MenuItem> {
    return this.updateMenu(id, { isAvailable: on });
  }
}
