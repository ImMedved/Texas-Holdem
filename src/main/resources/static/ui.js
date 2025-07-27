/*****************************************************************
 *  Exact Texas Hold'em odds front-end
 *  © kukharev.eu <<he-he>>
 *****************************************************************/

/* ----------   global state   --------------------------------- */
let seatsCount = 0;                          // current number of opponents

const used  = new Set();                     // already chosen cards
const hole  = [null, null];                  // hero hole cards
const board = [null, null, null, null, null];

const combos = [
    'High', 'Pair', 'Two pair', 'Set', 'Straight',
    'Flush', 'Full house', 'Quads', 'St-flush', 'Royal'
];

/* ----------   bootstrap   ------------------------------------ */
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('startBtn').onclick = onStart;
});

/* ----------   start / restart   ------------------------------ */
function onStart() {
    const n = +document.getElementById('oppInput').value;
    if (!Number.isInteger(n) || n < 1 || n > 8) {
        alert('enter 1-8'); return;
    }

    const root = document.getElementById('root');

    if (root.hidden) {           // first launch
        seatsCount = n;
        document.getElementById('setup').style.display = 'none';
        root.hidden = false;
        buildUI();
    } else {                     // second press → just reset
        resetUI(n);
    }
}

function buildUI() {
    buildSlots();
    buildSeats(seatsCount);
    buildTables();
    addControls();
    recalc();
}

function addControls() {
    const area = document.getElementById('table-area');
    if (document.getElementById('ctrls')) return;    // already present

    const box = document.createElement('div');
    box.id                 = 'ctrls';
    box.style.display      = 'flex';
    box.style.flexDirection= 'column';
    box.style.gap          = '6px';

    const reset = document.createElement('button');
    reset.textContent = 'Reset';
    reset.onclick = () => resetUI(seatsCount);

    const edit = document.createElement('button');
    edit.textContent = 'Opponents';
    edit.onclick = () => {
        const val = +prompt('Opponents (1-8)?', seatsCount) || seatsCount;
        if (val < 1 || val > 8) return;
        resetUI(val);
    };

    box.append(reset, edit);
    area.append(box);
}

function resetUI(newOpp) {
    used.clear();
    hole.fill(null);
    board.fill(null);

    document.querySelectorAll('.slot').forEach(s => {
        s.dataset.code = '';
        s.innerHTML = '';
    });

    buildSeats(newOpp);
    seatsCount = newOpp;

    document
        .querySelectorAll('#hero td:nth-child(2), #hero td:nth-child(3), #opps td:nth-child(2)')
        .forEach(td => td.textContent = '');
}

/* ----------   card slots   ----------------------------------- */
function buildSlots() {
    const make = () =>
        Object.assign(document.createElement('button'), { className: 'slot' });

    const boardDiv = document.getElementById('board');
    boardDiv.innerHTML = '';
    for (let i = 0; i < 5; i++) {
        const s = make();
        s.onclick = () => slotChoose(s, 'board', i);
        boardDiv.append(s);
    }

    const handDiv = document.getElementById('hero-hand');
    handDiv.innerHTML = '';
    for (let i = 0; i < 2; i++) {
        const s = make();
        s.onclick = () => slotChoose(s, 'hole', i);
        handDiv.append(s);
    }
}

/* ----------   card picker   ---------------------------------- */
const backdrop = document.getElementById('picker-backdrop');
const grid     = document.getElementById('grid');
const cancel   = document.getElementById('cancel');

let pickResolve = null;

cancel.onclick = () => finishPick(null);

function slotChoose(slot, where, idx) {
    chooseCard().then(code => {
        if (!code) return;

        /* free previously selected card */
        if (slot.dataset.code) used.delete(slot.dataset.code);

        /* render chosen card */
        slot.dataset.code = code;
        slot.innerHTML = '';
        slot.append(cardImg(code));
        used.add(code);

        /* update internal state */
        if (where === 'board') board[idx] = code;
        else                   hole[idx]  = code;

        recalc();
    });
}

function chooseCard() {
    renderPicker();
    backdrop.hidden = false;
    return new Promise(res => pickResolve = res);
}

function finishPick(code) {
    pickResolve(code);
    backdrop.hidden = true;
}

function renderPicker() {
    grid.innerHTML = '';
    const ranks = '23456789TJQKA';
    const suits = 'cdhs';

    for (const s of suits)
        for (const r of ranks) {
            const code = `${r}${s}`;
            const b = document.createElement('button');
            b.className = 'card-btn';
            if (used.has(code)) b.classList.add('used');
            b.append(cardImg(code));
            b.onclick = () =>
                !b.classList.contains('used') && finishPick(code);
            grid.append(b);
        }
}

const cardImg = code =>
    Object.assign(new Image(), { src: `./cards/${code}.png` });

/* ----------   opponents   ------------------------------------ */
function buildSeats(n) {
    const tmpl  = document.getElementById('seat-template');
    const table = document.getElementById('table');
    table.querySelectorAll('.seat').forEach(e => e.remove());

    const A = 0.80;                     // semi-axes (percent)
    const B = 0.60;
    const seats = n + 1;
    const skip  = Math.floor(seats / 2);    // hero bottom-center

    for (let s = 0, cnt = 0; cnt < n; s++) {
        if (s === skip) continue;

        const ang = -Math.PI / 2 + s * 2 * Math.PI / seats;
        const x   = 50 + 50 * A * Math.cos(ang);
        const y   = 50 + 50 * B * Math.sin(ang);

        const node = tmpl.content.cloneNode(true);
        const div  = node.querySelector('.seat');
        div.style.left = `${x}%`;
        div.style.top  = `${y}%`;

        const avatar  = div.querySelector('.avatar');
        const foldBtn = div.querySelector('.fold');
        const noneBtn = div.querySelector('.none');

        const markLeft = () => {
            avatar.src = './user/leftUser.jpg';
            foldBtn.disabled = true;
            noneBtn.disabled = true;
            recalc();
        };

        foldBtn.onclick = markLeft;
        noneBtn.onclick = markLeft;

        table.append(node);
        cnt++;
    }
}

/* ----------   odds tables   ---------------------------------- */
let heroTbl, oppTbl;

function buildTables() {
    heroTbl = document.getElementById('hero');
    oppTbl  = document.getElementById('opps');

    const addCols = tbl => {
        const cg = document.createElement('colgroup');
        ['3fr','1fr','3fr'].forEach(w => {
            const col = document.createElement('col');
            col.style.width = w;
            cg.append(col);
        });
        tbl.append(cg);
    };

    addCols(heroTbl);
    addCols(oppTbl);

    combos.forEach(c => {
        const tr = heroTbl.insertRow();
        tr.insertCell().textContent = c;
        tr.insertCell();
        tr.insertCell();
    });

    combos.slice(1).forEach(c => {
        const tr = oppTbl.insertRow();
        tr.insertCell().textContent = c;
        tr.insertCell();
        tr.insertCell();
    });
}

/* ----------   backend call   --------------------------------- */
function recalc() {
    if (hole.includes(null)) return;          // need both hole cards

    const activeOpp = Array
        .from(document.querySelectorAll('.seat .avatar'))
        .filter(img => img.src.endsWith('.onlineUser.jpg'))
        .length;

    const payload = {
        hole,
        board : board.filter(Boolean),
        activeOpp
    };

    fetch('/poker/api/probabilities', {
        method  : 'POST',
        headers : { 'Content-Type': 'application/json' },
        body    : JSON.stringify(payload)
    })
    .then(r => r.json())
    .then(({ hero, opp }) => {
        hero.forEach((p, i) =>
            heroTbl.rows[i].cells[1].textContent = (p*100).toFixed(2));
        hero.forEach((_, i) =>
            heroTbl.rows[i].cells[2].textContent = '');          // outs later

        opp.forEach((p, i) =>
            oppTbl.rows[i].cells[1].textContent = (p*100).toFixed(2));
    })
    .catch(e => console.error('backend', e));
}
