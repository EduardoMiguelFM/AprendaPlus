-- Script para verificar se as áreas de interesse estão sendo salvas

-- Ver todos os usuários e suas preferências
SELECT 
    u.id,
    u.nome,
    u.email,
    pu.onboarding_concluido,
    pu.id as preferencias_id
FROM usuarios u
LEFT JOIN preferencias_usuario pu ON pu.usuario_id = u.id
ORDER BY u.id;

-- Ver áreas de interesse cadastradas
SELECT 
    u.nome,
    u.email,
    ai.area,
    ni.nivel
FROM usuarios u
JOIN preferencias_usuario pu ON pu.usuario_id = u.id
LEFT JOIN areas_interesse ai ON ai.preferencias_id = pu.id
LEFT JOIN niveis_interesse ni ON ni.preferencias_id = pu.id
ORDER BY u.id, ai.area;

-- Contar quantas áreas cada usuário tem
SELECT 
    u.nome,
    u.email,
    COUNT(ai.area) as total_areas
FROM usuarios u
LEFT JOIN preferencias_usuario pu ON pu.usuario_id = u.id
LEFT JOIN areas_interesse ai ON ai.preferencias_id = pu.id
GROUP BY u.id, u.nome, u.email
ORDER BY u.id;


