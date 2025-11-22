// Script para gerenciar seleção de áreas de interesse
console.log("Script de áreas de interesse carregado");

let selectedAreas = [];

function toggleArea(checkbox) {
    console.log("toggleArea chamado", checkbox);
    const areaKey = checkbox.getAttribute("data-area");
    console.log("Área:", areaKey, "Checked:", checkbox.checked);

    if (!areaKey) {
        console.error("Área não encontrada no checkbox");
        return;
    }

    const nivelSelector = document.getElementById("nivel-" + areaKey);
    const card = checkbox.closest(".area-card");

    if (checkbox.checked) {
        if (selectedAreas.length >= 3) {
            checkbox.checked = false;
            alert("Você pode selecionar no máximo 3 áreas");
            return;
        }
        if (!selectedAreas.includes(areaKey)) {
            selectedAreas.push(areaKey);
            console.log("Área adicionada. Total:", selectedAreas.length);
        }
        if (nivelSelector) {
            nivelSelector.style.display = "block";
        }
        if (card) {
            card.classList.add("selected");
        }
    } else {
        const index = selectedAreas.indexOf(areaKey);
        if (index > -1) {
            selectedAreas.splice(index, 1);
            console.log("Área removida. Total:", selectedAreas.length);
        }
        if (nivelSelector) {
            nivelSelector.style.display = "none";
        }
        if (card) {
            card.classList.remove("selected");
        }
    }
    updateCounter();
    updateHiddenFields();
}

function updateCounter() {
    const count = selectedAreas.length;
    console.log("Atualizando contador:", count);

    const counterEl = document.getElementById("counter");
    const counterBtnEl = document.getElementById("counterBtn");
    const continueBtn = document.getElementById("continueBtn");

    if (counterEl) {
        counterEl.textContent = count;
    } else {
        console.error("Elemento counter não encontrado");
    }

    if (counterBtnEl) {
        counterBtnEl.textContent = count;
    } else {
        console.error("Elemento counterBtn não encontrado");
    }

    if (continueBtn) {
        continueBtn.disabled = count === 0 || count > 3;
    } else {
        console.error("Elemento continueBtn não encontrado");
    }
}

function updateHiddenFields() {
    const hiddenFields = document.getElementById("hiddenFields");
    if (!hiddenFields) {
        console.error("Elemento hiddenFields não encontrado");
        return;
    }

    const prefix = hiddenFields.getAttribute("data-prefix") || "";
    hiddenFields.innerHTML = "";

    selectedAreas.forEach((areaKey, index) => {
        const nivelSelect = document.getElementById("select-" + areaKey);
        const nivel = nivelSelect ? nivelSelect.value : "Iniciante";

        const areaInput = document.createElement("input");
        areaInput.type = "hidden";
        areaInput.name = prefix + "areasInteresse[" + index + "].area";
        areaInput.value = areaKey;
        hiddenFields.appendChild(areaInput);

        const nivelInput = document.createElement("input");
        nivelInput.type = "hidden";
        nivelInput.name = prefix + "areasInteresse[" + index + "].nivel";
        nivelInput.value = nivel;
        hiddenFields.appendChild(nivelInput);
    });

    console.log("Campos hidden atualizados. Total de áreas:", selectedAreas.length);
}

// Event delegation no documento
document.addEventListener("change", function (e) {
    if (e.target && e.target.classList.contains("area-checkbox")) {
        console.log("Evento change detectado no checkbox");
        toggleArea(e.target);
    }

    if (e.target && e.target.classList.contains("nivel-select")) {
        console.log("Nível alterado");
        updateHiddenFields();
    }
});

// Inicializar quando o DOM estiver pronto
function init() {
    console.log("Inicializando...");
    const checkboxes = document.querySelectorAll(".area-checkbox");
    console.log("Checkboxes encontrados:", checkboxes.length);

    if (checkboxes.length === 0) {
        console.warn("Nenhum checkbox encontrado ainda, tentando novamente...");
        setTimeout(init, 200);
        return;
    }

    selectedAreas = [];
    checkboxes.forEach(function (cb) {
        const area = cb.getAttribute("data-area");
        if (cb.checked) {
            if (!selectedAreas.includes(area)) {
                selectedAreas.push(area);
            }
            const nivelDiv = document.getElementById("nivel-" + area);
            if (nivelDiv) nivelDiv.style.display = "block";
            const card = cb.closest(".area-card");
            if (card) card.classList.add("selected");
        }
    });

    updateCounter();
    updateHiddenFields();
    console.log("Inicialização concluída!");
}

// Executar quando o DOM estiver pronto
if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
} else {
    // DOM já está pronto
    setTimeout(init, 100);
}

// Fallback adicional
window.addEventListener("load", function () {
    setTimeout(init, 200);
});


