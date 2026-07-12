/**
 * 商品模块 — 加入购物车（对接 ec 模块 POST /order/addCart）
 */
const CartUtil = {
    _userId: null,

    async getUserId() {
        if (this._userId != null) return this._userId;
        try {
            const res = await fetch('/user/info', { credentials: 'same-origin' });
            const data = await res.json();
            if (data.code === 200 && data.data && data.data.id) {
                this._userId = data.data.id;
                return this._userId;
            }
        } catch (e) {
            console.warn('获取登录用户失败', e);
        }
        return null;
    },

    async addToCart(product, quantity) {
        const userId = await this.getUserId();
        if (!userId) {
            if (confirm('请先登录后再加入购物车，是否前往登录？')) {
                location.href = '/user/login?redirect=' + encodeURIComponent(location.pathname + location.search);
            }
            return false;
        }

        const qty = quantity || 1;
        const body = {
            userId: userId,
            productId: product.id,
            productName: product.name || '',
            productImage: product.image || '',
            quantity: qty,
            price: product.price
        };

        try {
            const res = await fetch('/order/addCart', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify(body)
            });
            const text = await res.text();
            if (text === 'success') {
                return true;
            }
            alert('加入购物车失败：' + text);
            return false;
        } catch (e) {
            console.error(e);
            alert('加入购物车失败，请检查网络或是否已创建 cart_items 表');
            return false;
        }
    },

    cartUrl(userId) {
        return '/order/cart?userId=' + (userId || 1);
    },

    async initNavbar(navEl) {
        if (!navEl) return;
        const userId = await this.getUserId();
        if (userId) {
            navEl.innerHTML = `
                <a href="${this.cartUrl(userId)}" class="btn btn-outline-warning btn-sm me-2">🛒 购物车</a>
                <a href="/index" class="btn btn-link btn-sm text-decoration-none">个人中心</a>
            `;
        } else {
            navEl.innerHTML = `
                <a href="/user/login" class="btn btn-outline-primary btn-sm">登录</a>
            `;
        }
    },

    showToast(msg, ok) {
        let el = document.getElementById('cart-toast');
        if (!el) {
            el = document.createElement('div');
            el.id = 'cart-toast';
            el.style.cssText = 'position:fixed;top:70px;right:20px;z-index:9999;padding:12px 20px;border-radius:8px;color:#fff;font-size:14px;box-shadow:0 4px 12px rgba(0,0,0,.15);transition:opacity .3s';
            document.body.appendChild(el);
        }
        el.style.background = ok ? '#198754' : '#dc3545';
        el.textContent = msg;
        el.style.opacity = '1';
        setTimeout(() => { el.style.opacity = '0'; }, 2200);
    }
};
