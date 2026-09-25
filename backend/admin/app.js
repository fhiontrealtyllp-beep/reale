/* ═══════════════════════ Fhiont Admin Panel ═══════════════════════
 * Static SPA consuming the existing PHP API at ../api (same origin).
 * Auth: POST login.php → Bearer token reused on all endpoints.
 * ══════════════════════════════════════════════════════════════════ */

'use strict';

/* ── Config ── */
let API_BASE = (() => {
    const params = new URLSearchParams(location.search);
    if (params.get('api')) return params.get('api').replace(/\/+$/, '');
    return localStorage.getItem('fhiont_admin_api') || '../api';
})();

/**
 * Probes candidate API roots with the public locations.php endpoint and keeps
 * the first one that answers with valid API JSON. Handles both deploy layouts:
 *   api/admin/ → ../api      (backend dir copied as public_html/api)
 *   api/admin/ → ..          (php files copied directly into public_html/api)
 */
async function resolveApiBase() {
    if (new URLSearchParams(location.search).get('api') ||
        localStorage.getItem('fhiont_admin_api')) return;

    const candidates = ['../api', '..'];
    for (const base of candidates) {
        try {
            const ctrl = new AbortController();
            const timer = setTimeout(() => ctrl.abort(), 8000);
            const res = await fetch(base + '/locations.php', { signal: ctrl.signal });
            clearTimeout(timer);
            const json = await res.json();
            if (json && json.success === true) {
                API_BASE = base;
                return;
            }
        } catch (e) { /* try next */ }
    }
}

const STORE_TOKEN = 'fhiont_admin_token';
const STORE_USER = 'fhiont_admin_user';
const PAGE_SIZE = 100; // properties-all.php caps limit at 100
const COUNT_BATCH = 100;

/* ── State ── */
const state = {
    token: localStorage.getItem(STORE_TOKEN) || '',
    user: JSON.parse(localStorage.getItem(STORE_USER) || 'null'),
    section: 'all',
    allProperties: [],
    enquiryCounts: {},
    myEnquiries: [],
    filters: {
        search: '', city: '', locality: '', rentBuy: '',
        category: '', type: '', bedrooms: '', sort: 'newest'
    },
    detail: null
};

/* ── DOM ── */
const $ = (id) => document.getElementById(id);
const els = {
    loginView: $('loginView'), adminView: $('adminView'),
    loginForm: $('loginForm'), loginEmail: $('loginEmail'),
    loginPassword: $('loginPassword'), loginError: $('loginError'),
    loginBtn: $('loginBtn'),
    userName: $('userName'), logoutBtn: $('logoutBtn'),
    sectionTabs: $('sectionTabs'),
    fSearch: $('fSearch'), fCity: $('fCity'), fLocality: $('fLocality'),
    fRentBuy: $('fRentBuy'), fCategory: $('fCategory'), fType: $('fType'),
    fBedrooms: $('fBedrooms'), fSort: $('fSort'), resetFilters: $('resetFilters'),
    resultMeta: $('resultMeta'),
    propertyGrid: $('propertyGrid'), enquiryList: $('enquiryList'),
    emptyState: $('emptyState'), loadingState: $('loadingState'),
    statTotal: $('statTotal'), statFeatured: $('statFeatured'),
    statPromo: $('statPromo'), statBuy: $('statBuy'),
    statRent: $('statRent'), statCities: $('statCities'),
    detailModal: $('detailModal'), modalClose: $('modalClose'),
    dHero: $('dHero'), dThumbs: $('dThumbs'), dTitle: $('dTitle'),
    dLocation: $('dLocation'), dPrice: $('dPrice'), dBadges: $('dBadges'),
    dDescription: $('dDescription'), dFields: $('dFields'),
    dAmenities: $('dAmenities'), dDeleteBtn: $('dDeleteBtn'),
    dEnquiryCount: $('dEnquiryCount'), dEnquiries: $('dEnquiries'),
    toast: $('toast')
};

/* ══════════════ Helpers ══════════════ */

function esc(value) {
    return String(value ?? '').replace(/[&<>"']/g, (c) => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

function norm(value) { return String(value ?? '').trim().toLowerCase(); }

function toast(message, kind = '') {
    els.toast.textContent = message;
    els.toast.className = 'toast ' + kind;
    clearTimeout(toast._t);
    toast._t = setTimeout(() => els.toast.classList.add('hidden'), 3200);
}

function formatPrice(property) {
    const price = Number(property.price) || 0;
    const isRent = norm(property.rentBuy) === 'rent';
    if (isRent) return '₹' + price.toLocaleString('en-IN') + '/mo';
    if (price >= 10_000_000) return '₹' + (price / 10_000_000).toFixed(2).replace(/\.00$/, '') + ' Cr';
    if (price >= 100_000) return '₹' + (price / 100_000).toFixed(2).replace(/\.00$/, '') + ' L';
    return '₹' + price.toLocaleString('en-IN');
}

function formatDate(iso) {
    if (!iso) return '—';
    const d = new Date(String(iso).replace(' ', 'T'));
    if (Number.isNaN(d.getTime())) return String(iso);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

function formatDateTime(iso) {
    if (!iso) return '—';
    const d = new Date(String(iso).replace(' ', 'T'));
    if (Number.isNaN(d.getTime())) return String(iso);
    return d.toLocaleString('en-IN', {
        day: 'numeric', month: 'short', year: 'numeric',
        hour: 'numeric', minute: '2-digit'
    });
}

function pretty(value) {
    if (value === null || value === undefined || value === '') return '—';
    return String(value).replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase());
}

/* ══════════════ API client ══════════════ */

async function api(path, { method = 'GET', body } = {}) {
    const headers = {};
    if (body !== undefined) headers['Content-Type'] = 'application/json';
    if (state.token) headers['Authorization'] = 'Bearer ' + state.token;

    const url = API_BASE + path;
    const ctrl = new AbortController();
    const timer = setTimeout(() => ctrl.abort(), 15000);

    let res;
    try {
        res = await fetch(url, {
            method,
            headers,
            body: body !== undefined ? JSON.stringify(body) : undefined,
            signal: ctrl.signal
        });
    } catch (e) {
        if (e.name === 'AbortError') {
            throw new Error('Timed out: ' + url + ' (check that the API exists at ' + API_BASE + ')');
        }
        throw new Error('Network error: cannot reach ' + url);
    } finally {
        clearTimeout(timer);
    }

    let json;
    try {
        json = await res.json();
    } catch (e) {
        throw new Error('Bad response (' + res.status + ') from ' + path);
    }

    if (res.status === 401 && state.token) {
        forceLogout();
        throw new Error(json.message || 'Session expired');
    }
    if (!json.success) {
        throw new Error(json.message || 'Request failed (' + res.status + ')');
    }
    return json.data || {};
}

/* ══════════════ Auth ══════════════ */

async function login(email, password) {
    const data = await api('/login.php', { method: 'POST', body: { email, password } });
    const user = data.user || {};
    state.token = user.sessionId || '';
    state.user = user;
    localStorage.setItem(STORE_TOKEN, state.token);
    localStorage.setItem(STORE_USER, JSON.stringify(user));
    if (!state.token) throw new Error('Login succeeded but no session token returned');
}

async function logout() {
    try { await api('/logout.php', { method: 'POST' }); } catch (e) { /* ignore */ }
    forceLogout();
}

function forceLogout() {
    state.token = '';
    state.user = null;
    localStorage.removeItem(STORE_TOKEN);
    localStorage.removeItem(STORE_USER);
    showLogin();
}

function showLogin() {
    els.adminView.classList.add('hidden');
    els.loginView.classList.remove('hidden');
}

function showAdmin() {
    els.loginView.classList.add('hidden');
    els.adminView.classList.remove('hidden');
    els.userName.textContent = state.user?.name || state.user?.email || '';
}

/* ══════════════ Data loading ══════════════ */

async function loadAllProperties() {
    const collected = [];
    let page = 0;
    for (;;) {
        const data = await api(`/properties-all.php?page=${page}&limit=${PAGE_SIZE}`);
        const chunk = data.properties || [];
        collected.push(...chunk);
        const pg = data.pagination || {};
        if (!pg.hasMore || chunk.length === 0) break;
        page += 1;
        if (page > 50) break; // safety
    }
    state.allProperties = dedupeById(collected);
}

function dedupeById(list) {
    const seen = new Set();
    return list.filter((p) => {
        const key = String(p.id);
        if (seen.has(key)) return false;
        seen.add(key);
        return true;
    });
}

async function loadEnquiryCounts() {
    const ids = state.allProperties.map((p) => String(p.id)).filter((id) => /^\d+$/.test(id));
    const counts = {};
    for (let i = 0; i < ids.length; i += COUNT_BATCH) {
        const batch = ids.slice(i, i + COUNT_BATCH);
        try {
            const data = await api('/enquiry-counts.php?ids=' + encodeURIComponent(batch.join(',')));
            Object.assign(counts, data.counts || {});
        } catch (e) { /* counts are best-effort */ }
    }
    state.enquiryCounts = counts;
}

async function loadMyEnquiries() {
    const data = await api('/enquiries.php?type=user');
    state.myEnquiries = data.enquiries || [];
}

async function loadLocations() {
    try {
        const data = await api('/locations.php');
        state.cities = data.cities || [];
        state.localities = data.localities || [];
    } catch (e) {
        state.cities = [];
        state.localities = [];
    }
}

async function refreshAll() {
    els.loadingState.classList.remove('hidden');
    try {
        await loadAllProperties();
        renderStats();
        populateFilterOptions();
        loadEnquiryCounts().then(renderContent);
        if (state.section === 'enquiries') await loadMyEnquiries();
        renderContent();
    } catch (e) {
        toast(e.message, 'error');
        els.loadingState.classList.add('hidden');
    }
}

/* ══════════════ Stats ══════════════ */

function renderStats() {
    const all = state.allProperties;
    els.statTotal.textContent = all.length;
    els.statFeatured.textContent = all.filter((p) => norm(p.listingCategory) === 'featured').length;
    els.statPromo.textContent = all.filter((p) => norm(p.listingCategory) === 'promotional').length;
    els.statBuy.textContent = all.filter((p) => norm(p.rentBuy) === 'buy').length;
    els.statRent.textContent = all.filter((p) => norm(p.rentBuy) === 'rent').length;
    els.statCities.textContent = new Set(all.map((p) => norm(p.city)).filter(Boolean)).size;
}

/* ══════════════ Filter options ══════════════ */

function uniqueValues(key) {
    return [...new Set(state.allProperties.map((p) => String(p[key] ?? '').trim()).filter(Boolean))]
        .sort((a, b) => a.localeCompare(b));
}

function setOptions(select, values, keepFirst) {
    const first = select.options[0];
    select.innerHTML = '';
    if (keepFirst) select.appendChild(first);
    values.forEach((v) => {
        const opt = document.createElement('option');
        opt.value = v;
        opt.textContent = pretty(v);
        select.appendChild(opt);
    });
}

function populateFilterOptions() {
    const savedCity = els.fCity.value;
    const cities = state.cities?.length ? state.cities : uniqueValues('city');
    setOptions(els.fCity, cities, true);
    els.fCity.value = savedCity;

    populateLocalityOptions();
    setOptions(els.fType, uniqueValues('propertyType'), true);
    setOptions(els.fBedrooms, uniqueValues('bedroomType'), true);
}

function populateLocalityOptions() {
    const city = els.fCity.value;
    const saved = els.fLocality.value;
    let values;
    if (city && state.localities?.length) {
        values = state.localities
            .filter((l) => norm(l.city) === norm(city))
            .map((l) => l.locality);
    } else {
        values = state.allProperties
            .filter((p) => !city || norm(p.city) === norm(city))
            .map((p) => p.locality);
    }
    values = [...new Set(values.filter(Boolean))].sort((a, b) => a.localeCompare(b));
    setOptions(els.fLocality, values, true);
    els.fLocality.value = values.includes(saved) ? saved : '';
}

/* ══════════════ Filtering / sorting ══════════════ */

function sectionBaseList() {
    const all = state.allProperties;
    switch (state.section) {
        case 'featured':
            return all.filter((p) => norm(p.listingCategory) === 'featured');
        case 'promotional':
            return all.filter((p) => norm(p.listingCategory) === 'promotional');
        case 'mine':
            return all.filter((p) => String(p.userId) === String(state.user?.id));
        default:
            return all;
    }
}

function applyFilters(list) {
    const f = state.filters;
    let out = list;

    if (f.search) {
        const q = f.search.toLowerCase();
        out = out.filter((p) =>
            norm(p.title).includes(q) ||
            norm(p.locality).includes(q) ||
            norm(p.address).includes(q) ||
            norm(p.city).includes(q)
        );
    }
    if (f.city) out = out.filter((p) => norm(p.city) === norm(f.city));
    if (f.locality) out = out.filter((p) => norm(p.locality) === norm(f.locality));
    if (f.rentBuy) out = out.filter((p) => norm(p.rentBuy) === f.rentBuy);
    if (f.category) out = out.filter((p) => norm(p.listingCategory) === norm(f.category));
    if (f.type) out = out.filter((p) => norm(p.propertyType) === norm(f.type));
    if (f.bedrooms) out = out.filter((p) => norm(p.bedroomType) === norm(f.bedrooms));

    const sorted = [...out];
    switch (f.sort) {
        case 'oldest': sorted.sort((a, b) => String(a.createdAt).localeCompare(String(b.createdAt))); break;
        case 'price_low': sorted.sort((a, b) => (a.price || 0) - (b.price || 0)); break;
        case 'price_high': sorted.sort((a, b) => (b.price || 0) - (a.price || 0)); break;
        case 'title': sorted.sort((a, b) => String(a.title).localeCompare(String(b.title))); break;
        default: sorted.sort((a, b) => String(b.createdAt).localeCompare(String(a.createdAt)));
    }
    return sorted;
}

/* ══════════════ Rendering ══════════════ */

function renderContent() {
    els.loadingState.classList.add('hidden');

    if (state.section === 'enquiries') {
        renderEnquirySection();
        return;
    }

    els.enquiryList.classList.add('hidden');
    els.propertyGrid.classList.remove('hidden');

    const list = applyFilters(sectionBaseList());
    els.resultMeta.innerHTML =
        `Showing <b>${list.length}</b> of <b>${state.allProperties.length}</b> properties`;

    if (list.length === 0) {
        els.propertyGrid.innerHTML = '';
        els.emptyState.classList.remove('hidden');
        return;
    }
    els.emptyState.classList.add('hidden');
    els.propertyGrid.innerHTML = list.map(cardHtml).join('');

    els.propertyGrid.querySelectorAll('.p-card').forEach((card) => {
        card.addEventListener('click', () => openDetail(card.dataset.id));
    });
}

function cardHtml(p) {
    const img = (p.images || [])[0];
    const cat = norm(p.listingCategory);
    const rb = norm(p.rentBuy);
    const enq = state.enquiryCounts[String(p.id)] || 0;
    const meta = [
        p.bedroomType ? pretty(p.bedroomType) : null,
        p.bathrooms != null ? p.bathrooms + ' bath' : null,
        p.propertyType ? pretty(p.propertyType) : null,
        p.carpetArea ? Math.round(p.carpetArea) + ' sqft' : null
    ].filter(Boolean);

    return `
    <article class="p-card" data-id="${esc(p.id)}">
        <div class="p-thumb">
            ${img ? `<img src="${esc(img)}" loading="lazy" alt="">` : '<div class="no-img">⌂</div>'}
            ${rb ? `<span class="p-rentbuy ${rb}">${esc(rb)}</span>` : ''}
            ${cat ? `<span class="p-category ${cat}">${esc(cat)}</span>` : ''}
            <span class="p-price">${esc(formatPrice(p))}</span>
        </div>
        <div class="p-body">
            <h3 class="p-title">${esc(p.title)}</h3>
            <p class="p-loc">${esc([p.locality, p.city].filter(Boolean).join(', '))}</p>
            <div class="p-chips">${meta.map((m) => `<span class="chip">${esc(m)}</span>`).join('')}</div>
            <div class="p-footer">
                <span class="p-id">#${esc(p.id)} · ${esc(formatDate(p.createdAt))}</span>
                <span class="p-enq">${enq ? enq + ' enquiries' : ''}</span>
            </div>
        </div>
    </article>`;
}

function renderEnquirySection() {
    els.propertyGrid.classList.add('hidden');
    els.propertyGrid.innerHTML = '';
    els.enquiryList.classList.remove('hidden');
    els.resultMeta.innerHTML = `<b>${state.myEnquiries.length}</b> enquiries sent by you`;

    if (!state.myEnquiries.length) {
        els.enquiryList.innerHTML = '';
        els.emptyState.classList.remove('hidden');
        return;
    }
    els.emptyState.classList.add('hidden');
    els.enquiryList.innerHTML = state.myEnquiries.map((e) => `
        <div class="e-card">
            ${e.propertyImage ? `<img class="e-thumb" src="${esc(e.propertyImage)}" alt="">` : ''}
            <div class="e-body">
                <div class="e-title">${esc(e.propertyTitle || 'Property #' + e.propertyId)}</div>
                <div class="e-loc">${esc(e.propertyLocation || '')}</div>
                <div class="e-msg">${esc(e.message)}</div>
                <div class="e-meta">
                    <span>${esc(formatDateTime(e.createdAt))}</span>
                    ${e.agentPhone ? `<span>Agent: ${esc(e.agentPhone)}</span>` : ''}
                </div>
            </div>
            <span class="e-status">${esc(e.status || 'new')}</span>
        </div>`).join('');
}

/* ══════════════ Detail modal ══════════════ */

function openDetail(id) {
    const p = state.allProperties.find((x) => String(x.id) === String(id));
    if (!p) return;
    state.detail = p;

    const images = p.images || [];
    els.dHero.src = images[0] || '';
    els.dHero.parentElement.style.display = images.length ? '' : 'none';
    els.dThumbs.innerHTML = images.slice(1).map((src, i) =>
        `<img src="${esc(src)}" data-i="${i + 1}" alt="">`).join('');
    els.dThumbs.querySelectorAll('img').forEach((t) => {
        t.addEventListener('click', () => {
            els.dHero.src = t.src;
            els.dThumbs.querySelectorAll('img').forEach((x) => x.classList.remove('active'));
            t.classList.add('active');
        });
    });

    els.dTitle.textContent = p.title || 'Untitled';
    els.dLocation.textContent = [p.address || [p.locality, p.city].filter(Boolean).join(', '), p.pincode]
        .filter(Boolean).join(' — ');
    els.dPrice.textContent = formatPrice(p);

    const badges = [];
    const cat = norm(p.listingCategory);
    if (cat === 'featured') badges.push('<span class="d-badge hl-featured">★ Featured</span>');
    if (cat === 'promotional') badges.push('<span class="d-badge hl-promo">◆ Promotional</span>');
    const rb = norm(p.rentBuy);
    if (rb) badges.push(`<span class="d-badge hl-${rb}">For ${esc(rb)}</span>`);
    if (p.propertyType) badges.push(`<span class="d-badge">${esc(pretty(p.propertyType))}</span>`);
    if (p.residentialCommercial) badges.push(`<span class="d-badge">${esc(pretty(p.residentialCommercial))}</span>`);
    if (p.status) badges.push(`<span class="d-badge">${esc(p.status)}</span>`);
    els.dBadges.innerHTML = badges.join('');

    els.dDescription.textContent = p.description || '';

    const fields = [
        ['Property ID', '#' + p.id],
        ['Owner ID', p.userId],
        ['Bedrooms', pretty(p.bedroomType)],
        ['Bathrooms', p.bathrooms],
        ['Furnishing', pretty(p.furnishing)],
        ['Facing', pretty(p.facing)],
        ['Age', pretty(p.age)],
        ['Carpet area', p.carpetArea ? p.carpetArea + ' sqft' : '—'],
        ['Built-up area', p.builtUpArea ? p.builtUpArea + ' sqft' : '—'],
        ['Super built-up', p.superBuiltUpArea ? p.superBuiltUpArea + ' sqft' : '—'],
        ['Latitude', p.latitude],
        ['Longitude', p.longitude],
        ['Agent phone', p.agentPhone],
        ['Created', formatDateTime(p.createdAt)]
    ];
    els.dFields.innerHTML = fields.map(([k, v]) =>
        `<div><dt>${esc(k)}</dt><dd>${esc(v ?? '—')}</dd></div>`).join('');

    const amenities = Array.isArray(p.amenities) ? p.amenities : [];
    els.dAmenities.innerHTML = amenities.length
        ? amenities.map((a) => `<span class="chip">${esc(pretty(a))}</span>`).join('')
        : '';

    // Delete: backend only allows owner to delete
    const isOwner = String(p.userId) === String(state.user?.id);
    els.dDeleteBtn.classList.toggle('hidden', !isOwner);
    els.dDeleteBtn.onclick = () => deleteProperty(p);

    // Enquiries for this property
    els.dEnquiryCount.textContent = '';
    els.dEnquiries.innerHTML = '<div class="d-enquiry"><span class="e-none">Loading…</span></div>';
    api('/enquiries.php?type=property&propertyId=' + encodeURIComponent(p.id))
        .then((data) => {
            if (state.detail !== p) return;
            const list = data.enquiries || [];
            els.dEnquiryCount.textContent = `(${list.length})`;
            els.dEnquiries.innerHTML = list.length
                ? list.map((e) => `
                    <div class="d-enquiry">
                        ${esc(e.message)}
                        <div class="e-when">${esc(formatDateTime(e.createdAt))} · user ${esc(e.userId || '—')} · ${esc(e.status)}</div>
                    </div>`).join('')
                : '<div class="d-enquiry"><span class="e-none">No enquiries yet.</span></div>';
        })
        .catch(() => {
            if (state.detail !== p) return;
            els.dEnquiries.innerHTML = '<div class="d-enquiry"><span class="e-none">Could not load enquiries.</span></div>';
        });

    els.detailModal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeDetail() {
    state.detail = null;
    els.detailModal.classList.add('hidden');
    document.body.style.overflow = '';
}

async function deleteProperty(p) {
    if (!confirm(`Delete "${p.title}" (#${p.id})?\nThis also removes its images, likes and enquiries.`)) return;
    els.dDeleteBtn.disabled = true;
    try {
        await api('/delete-property.php', {
            method: 'POST',
            body: { propertyId: Number(p.id) }
        });
        state.allProperties = state.allProperties.filter((x) => String(x.id) !== String(p.id));
        closeDetail();
        renderStats();
        renderContent();
        toast('Property deleted', 'ok');
    } catch (e) {
        toast(e.message, 'error');
        els.dDeleteBtn.disabled = false;
    }
}

/* ══════════════ Events ══════════════ */

function bindEvents() {
    els.loginForm.addEventListener('submit', async (ev) => {
        ev.preventDefault();
        els.loginError.classList.add('hidden');
        els.loginBtn.disabled = true;
        els.loginBtn.textContent = 'Signing in…';
        try {
            await login(els.loginEmail.value.trim(), els.loginPassword.value);
            showAdmin();
            refreshAll();
        } catch (e) {
            els.loginError.textContent = e.message;
            els.loginError.classList.remove('hidden');
        } finally {
            els.loginBtn.disabled = false;
            els.loginBtn.textContent = 'Sign in';
        }
    });

    els.logoutBtn.addEventListener('click', logout);

    els.sectionTabs.addEventListener('click', async (ev) => {
        const tab = ev.target.closest('.tab');
        if (!tab || tab.dataset.section === state.section) return;
        state.section = tab.dataset.section;
        els.sectionTabs.querySelectorAll('.tab').forEach((t) => t.classList.toggle('active', t === tab));
        if (state.section === 'enquiries') {
            try { await loadMyEnquiries(); } catch (e) { toast(e.message, 'error'); }
        }
        renderContent();
    });

    els.fSearch.addEventListener('input', () => { state.filters.search = els.fSearch.value; renderContent(); });
    els.fCity.addEventListener('change', () => {
        state.filters.city = els.fCity.value;
        state.filters.locality = '';
        populateLocalityOptions();
        renderContent();
    });
    els.fLocality.addEventListener('change', () => { state.filters.locality = els.fLocality.value; renderContent(); });
    els.fRentBuy.addEventListener('change', () => { state.filters.rentBuy = els.fRentBuy.value; renderContent(); });
    els.fCategory.addEventListener('change', () => { state.filters.category = els.fCategory.value; renderContent(); });
    els.fType.addEventListener('change', () => { state.filters.type = els.fType.value; renderContent(); });
    els.fBedrooms.addEventListener('change', () => { state.filters.bedrooms = els.fBedrooms.value; renderContent(); });
    els.fSort.addEventListener('change', () => { state.filters.sort = els.fSort.value; renderContent(); });
    els.resetFilters.addEventListener('click', () => {
        state.filters = { search: '', city: '', locality: '', rentBuy: '', category: '', type: '', bedrooms: '', sort: 'newest' };
        els.fSearch.value = '';
        els.fCity.value = '';
        populateLocalityOptions();
        els.fLocality.value = '';
        els.fRentBuy.value = '';
        els.fCategory.value = '';
        els.fType.value = '';
        els.fBedrooms.value = '';
        els.fSort.value = 'newest';
        renderContent();
    });

    els.modalClose.addEventListener('click', closeDetail);
    els.detailModal.addEventListener('click', (ev) => {
        if (ev.target === els.detailModal) closeDetail();
    });
    document.addEventListener('keydown', (ev) => {
        if (ev.key === 'Escape' && state.detail) closeDetail();
    });
}

/* ══════════════ Boot ══════════════ */

window.addEventListener('error', (ev) => {
    toast('Script error: ' + (ev.message || 'unknown'), 'error');
});

bindEvents();
resolveApiBase().then(() => {
    if (state.token) {
        showAdmin();
        loadLocations();
        refreshAll();
    } else {
        showLogin();
        loadLocations();
    }
});
